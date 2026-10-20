package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.common.Directories;
import notkirill.workshopmanager.common.Zips;
import notkirill.workshopmanager.content.enums.ContentJobError;
import notkirill.workshopmanager.content.fetch.ContentFetchException;
import notkirill.workshopmanager.content.fetch.ContentFetcher;
import notkirill.workshopmanager.content.fetch.FetchedContent;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.steam.models.SteamWorkshopItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

@Component
public class SteamContentFetcher implements ContentFetcher {
    private static final Logger log = LoggerFactory.getLogger(SteamContentFetcher.class);
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private static final Duration RESPONSE_MARGIN = Duration.ofMinutes(1);

    private final SteamProperties properties;
    private final SteamItemReaderService itemReaderService;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    @Autowired
    public SteamContentFetcher(SteamProperties properties, SteamItemReaderService itemReaderService) {
        this.properties = properties;
        this.itemReaderService = itemReaderService;
    }

    @Override
    public ModspaceType type() {
        return ModspaceType.STEAM;
    }

    @Override
    public boolean isAvailable() {
        return properties.steamcmd().enabled();
    }

    @Override
    public FetchedContent fetch(String originalId, Path workDir) {
        SteamWorkshopItem item = itemReaderService.findItem(originalId)
                .orElseThrow(() -> new ContentFetchException(
                        ContentJobError.DOWNLOAD_FAILED,
                        "Steam metadata of the item is missing", true
                ));
        String folder = workDir.getFileName().toString();
        Path downloaded = properties.steamcmd().downloadsDir().resolve(folder);
        try {
            String content = download(item, folder);
            Path zip = workDir.resolve(item.getPublishedFileId() + ".zip");
            Zips.zipDirectory(downloaded.resolve(content), zip);
            String title = item.getTitle() == null || item.getTitle().isBlank()
                    ? item.getPublishedFileId() : item.getTitle();
            return new FetchedContent(zip, title + ".zip");
        } catch (IOException e) {
            log.warn("SteamCMD download of item {} failed: {}", item.getPublishedFileId(), e.getMessage());
            throw new ContentFetchException(
                    ContentJobError.DOWNLOAD_FAILED, "SteamCMD download failed: "
                    + e.getMessage(), e, true
            );
        } finally {
            Directories.deleteRecursively(downloaded);
        }
    }

    private String download(SteamWorkshopItem item, String folder) throws IOException {
        SteamProperties.SteamCmd steamCmd = properties.steamcmd();
        URI uri = steamCmd.url().resolve("/download?app_id=" + item.getConsumerAppId()
                                                 + "&file_id=" + item.getPublishedFileId() + "&folder=" + folder + "&timeout=" + steamCmd.timeout());
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(steamCmd.timeout().plus(RESPONSE_MARGIN))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ContentFetchException(ContentJobError.DOWNLOAD_FAILED, "SteamCMD download interrupted", e, true);
        }
        if (response.statusCode() != 200) {
            // 503: another download is running
            throw new ContentFetchException(
                    ContentJobError.DOWNLOAD_FAILED, "SteamCMD service answered HTTP "
                    + response.statusCode() + ": " + response.body(), true
            );
        }
        JsonNode result = JSON_MAPPER.readTree(response.body());
        String message = result.path("message").asString();
        return switch (result.path("status").asString()) {
            case "OK" -> result.path("content").asString();
            case "TIMEOUT" -> throw new ContentFetchException(ContentJobError.DOWNLOAD_TIMEOUT, message, true);
            case "NOT_DOWNLOADABLE" -> throw new ContentFetchException(
                    ContentJobError.NOT_DOWNLOADABLE, message,
                    false
            );
            default -> throw new ContentFetchException(ContentJobError.DOWNLOAD_FAILED, message, true);
        };
    }
}
