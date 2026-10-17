package notkirill.workshopmanager.media.source;

import notkirill.workshopmanager.media.MediaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;

@Component
public class SourceMediaDownloader {
    private static final int MAX_REDIRECTS = 3;
    private static final Set<Integer> REDIRECTS = Set.of(301, 302, 303, 307, 308);

    private final MediaProperties properties;
    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    public SourceMediaDownloader(MediaProperties properties) {
        this.properties = properties;
    }

    private static void requireAllowed(URI url, Set<String> allowedHosts) {
        String host = url.getHost() == null ? "" : url.getHost().toLowerCase(Locale.ROOT);
        if (!"https".equalsIgnoreCase(url.getScheme()) || !allowedHosts.contains(host)) {
            throw new SourceMediaException("Not an allowed download location: " + url, true);
        }
    }

    private static String fileName(URI url) {
        String path = url.getPath() == null ? "" : url.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public DownloadedFile download(URI url, Set<String> allowedHosts) {
        long maxSize = properties.maxImageSize().toBytes();
        URI current = url;
        for (int redirect = 0; redirect <= MAX_REDIRECTS; redirect++) {
            requireAllowed(current, allowedHosts);
            HttpResponse<InputStream> response = send(current);
            try (InputStream body = response.body()) {
                int status = response.statusCode();
                if (REDIRECTS.contains(status)) {
                    current = current.resolve(response.headers().firstValue("Location")
                                                      .orElseThrow(() -> new SourceMediaException(
                                                              "Redirect without a location", true)));
                    continue;
                }
                if (status != 200) {
                    // Missing files stay missing; anything else may be temporary
                    throw new SourceMediaException(
                            "HTTP " + status + " for " + current, status == 404 || status == 410);
                }
                if (response.headers().firstValueAsLong("Content-Length").orElse(0) > maxSize) {
                    throw new SourceMediaException("Larger than " + properties.maxImageSize(), true);
                }
                byte[] data = body.readNBytes((int) maxSize + 1);
                if (data.length > maxSize) {
                    throw new SourceMediaException("Larger than " + properties.maxImageSize(), true);
                }
                return new DownloadedFile(data, fileName(current));
            } catch (IOException e) {
                throw new SourceMediaException("Download of " + current + " failed: " + e.getMessage(), e, false);
            }
        }
        throw new SourceMediaException("Too many redirects for " + url, true);
    }

    private HttpResponse<InputStream> send(URI url) {
        HttpRequest request = HttpRequest.newBuilder(url).timeout(properties.downloadTimeout()).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException e) {
            throw new SourceMediaException("Download of " + url + " failed: " + e.getMessage(), e, false);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SourceMediaException("Download of " + url + " interrupted", e, false);
        }
    }

    public record DownloadedFile(byte[] data, String fileName) {
    }
}
