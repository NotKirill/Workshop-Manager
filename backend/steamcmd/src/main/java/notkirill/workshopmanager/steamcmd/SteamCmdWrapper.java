package notkirill.workshopmanager.steamcmd;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * Runs SteamCMD inside its own container.
 * <p>
 * {@code POST /download?app_id=4000&file_id=160250458&folder=job-12-x&timeout=PT30M} downloads one workshop item into
 * {@code /downloads/{folder}}, a folder shared with the backend, and answers with JSON:
 * {@code {"status":"OK","content":"<the item's folder inside {folder}>"}} or
 * {@code {"status":"TIMEOUT|NOT_DOWNLOADABLE|FAILED","message":"..."}}. The backend deletes the folder when it is done.
 * One download runs at a time; another request meanwhile gets 503. {@code GET /health} answers 200.
 * <p>
 * Right after start SteamCMD updates itself (into the {@code /opt/steamcmd} volume, so the update is kept); downloads
 * get 503 meanwhile. Every request writes one line to standard output ({@code docker compose logs steamcmd}).
 */
public final class SteamCmdWrapper {
    private static final Pattern ID = Pattern.compile("[/d]{1,20}");
    private static final Pattern FOLDER = Pattern.compile("[A-Za-z0-9-]{1,100}");
    private static final Pattern ANSI_CODE = Pattern.compile("\u001B\\[[0-9;]*[A-Za-z]");

    private static final int OUTPUT_TAIL_BYTES = 4096;
    private static final Duration UPDATE_TIMEOUT = Duration.ofMinutes(30);

    private final Path steamCmd;
    private final Path downloads;
    private final ReentrantLock busy = new ReentrantLock();

    private SteamCmdWrapper(Path steamCmd, Path downloads) {
        this.steamCmd = steamCmd;
        this.downloads = downloads;
    }

    static void main() throws IOException {
        SteamCmdWrapper wrapper = new SteamCmdWrapper(
                Path.of(env("STEAMCMD", "/opt/steamcmd/steamcmd.sh")),
                Path.of(env("DOWNLOADS", "/downloads"))
        );
        int port = Integer.parseInt(env("PORT", "8095"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/download", wrapper::download);
        server.createContext(
                "/health", exchange -> {
                    respond(exchange, 200, "text/plain", "ok");
                    exchange.close();
                }
        );
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        log("SteamCMD wrapper listening on port " + port);
        Thread.ofVirtual().start(wrapper::update);
    }

    private static void log(String message) {
        System.out.println(Instant.now().truncatedTo(ChronoUnit.SECONDS) + " " + message);
    }

    private static String seconds(long startNanos) {
        return String.format(Locale.ROOT, "%.1fs", (System.nanoTime() - startNanos) / 1e9);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> query = new HashMap<>();
        if (rawQuery == null) {
            return query;
        }
        for (String pair : rawQuery.split("&")) {
            int separator = pair.indexOf('=');
            if (separator > 0) {
                query.put(
                        URLDecoder.decode(pair.substring(0, separator), StandardCharsets.UTF_8),
                        URLDecoder.decode(pair.substring(separator + 1), StandardCharsets.UTF_8)
                );
            }
        }
        return query;
    }

    private static String require(Map<String, String> query, String name, Pattern format) {
        String value = query.get(name);
        if (value == null || !format.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid or missing " + name);
        }
        return value;
    }

    private static Duration parseTimeout(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing timeout");
        }
        Duration timeout;
        try {
            timeout = Duration.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid timeout", e);
        }
        if (!timeout.isPositive()) {
            throw new IllegalArgumentException("The timeout must be positive");
        }
        return timeout;
    }

    private static String lastLines(Path output) {
        String tail;
        try (RandomAccessFile in = new RandomAccessFile(output.toFile(), "r")) {
            long start = Math.max(0, in.length() - OUTPUT_TAIL_BYTES);
            byte[] bytes = new byte[(int) (in.length() - start)];
            in.seek(start);
            in.readFully(bytes);
            tail = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "(output not readable: " + e.getMessage() + ")";
        }
        List<String> lines = ANSI_CODE.matcher(tail).replaceAll("").lines()
                .map(String::strip)
                .filter(line -> !line.isEmpty())
                .toList();
        return String.join(" | ", lines.subList(Math.max(0, lines.size() - 5), lines.size()));
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                default -> {
                    if (c < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
                }
            }
        }
        return escaped.toString();
    }

    private static void respond(HttpExchange exchange, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private void download(HttpExchange exchange) throws IOException {
        try {
            if (!exchange.getRequestMethod().equals("POST")) {
                respond(exchange, 405, "text/plain", "POST only");
                return;
            }
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            String appId = require(query, "app_id", ID);
            String fileId = require(query, "file_id", ID);
            String folder = require(query, "folder", FOLDER);
            Duration timeout = parseTimeout(query.get("timeout"));
            if (!busy.tryLock()) {
                log("download " + appId + "/" + fileId + " refused: SteamCMD is busy");
                respond(exchange, 503, "text/plain", "SteamCMD is busy with another download or its update");
                return;
            }
            Result result;
            long start = System.nanoTime();
            try {
                result = run(appId, fileId, folder, timeout);
            } finally {
                busy.unlock();
            }
            log("download " + appId + "/" + fileId + " into " + folder + ": " + result.status() + " in "
                        + seconds(start) + (result.status().equals("OK") ? "" : " - " + result.value()));
            respond(exchange, 200, "application/json", result.toJson());
        } catch (IllegalArgumentException e) {
            log("bad request: " + e.getMessage());
            respond(exchange, 400, "text/plain", e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private Result run(String appId, String fileId, String folder, Duration timeout) {
        Path installDir = downloads.resolve(folder);
        Path output = installDir.resolve("steamcmd-output.log");
        OptionalInt exitCode;
        try {
            Files.createDirectories(installDir);
            exitCode = runSteamCmd(
                    output, timeout,
                    "+force_install_dir", installDir.toString(),
                    "+login", "anonymous",
                    "+workshop_download_item", appId, fileId, "validate",
                    "+quit"
            );
        } catch (IOException e) {
            return new Result("FAILED", "message", "SteamCMD could not start: " + e.getMessage());
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            return new Result("FAILED", "message", "Interrupted while waiting for SteamCMD");
        }
        if (exitCode.isEmpty()) {
            return new Result(
                    "TIMEOUT", "message", "SteamCMD did not finish within " + timeout + ": "
                    + lastLines(output)
            );
        }

        String content = "steamapps/workshop/content/" + appId + "/" + fileId;
        if (Files.isDirectory(installDir.resolve(content))) {
            return new Result("OK", "content", content);
        }
        String tail = lastLines(output);
        boolean ownershipIssue = tail.contains("Failure") || tail.contains("No subscription");
        return new Result(
                ownershipIssue ? "NOT_DOWNLOADABLE" : "FAILED", "message",
                "SteamCMD did not download the item (exit " + exitCode.getAsInt() + "): " + tail
        );
    }

    private void update() {
        busy.lock();
        try {
            log("SteamCMD update started");
            long start = System.nanoTime();
            Path output = Files.createTempFile("steamcmd-update", ".log");
            try {
                OptionalInt exitCode = runSteamCmd(output, UPDATE_TIMEOUT, "+quit");
                if (exitCode.isEmpty()) {
                    log("SteamCMD update did not finish within " + UPDATE_TIMEOUT + ": " + lastLines(output));
                } else if (exitCode.getAsInt() != 0) {
                    log("SteamCMD update failed (exit " + exitCode.getAsInt() + "): " + lastLines(output));
                } else {
                    log("SteamCMD is up to date (" + seconds(start) + ")");
                }
            } finally {
                Files.deleteIfExists(output);
            }
        } catch (IOException e) {
            log("SteamCMD update failed: " + e.getMessage());
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            log("SteamCMD update interrupted");
        } finally {
            busy.unlock();
        }
    }

    private OptionalInt runSteamCmd(Path output, Duration timeout, String... arguments)
            throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add(steamCmd.toString());
        command.addAll(List.of(arguments));
        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(output.toFile())
                .start();
        try {
            return process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)
                    ? OptionalInt.of(process.exitValue())
                    : OptionalInt.empty();
        } finally {
            // SteamCMD runs as a script that starts the real program as a child process
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process.destroyForcibly();
        }
    }

    private record Result(String status, String key, String value) {
        String toJson() {
            return "{\"status\":\"" + status + "\",\"" + key + "\":\"" + escapeJson(value) + "\"}";
        }
    }
}
