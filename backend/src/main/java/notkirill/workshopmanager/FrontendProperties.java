package notkirill.workshopmanager;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.Locale;

@ConfigurationProperties("application.frontend")
@Validated
public record FrontendProperties(
        @Name("url") @NotNull URI url
) {
    public FrontendProperties {
        if (url == null || !isOrigin(url)) {
            throw new IllegalArgumentException("application.frontend.url must be an address like "
                                                       + "https://workshop.example.com, without a path, query or fragment: " + url);
        }
        url = URI.create(url.getScheme().toLowerCase(Locale.ROOT) + "://" + url.getRawAuthority());
    }

    private static boolean isOrigin(URI url) {
        String path = url.getRawPath();
        return ("http".equalsIgnoreCase(url.getScheme()) || "https".equalsIgnoreCase(url.getScheme()))
                && url.getHost() != null
                && url.getRawUserInfo() == null
                && (path == null || path.isEmpty() || path.equals("/"))
                && url.getRawQuery() == null
                && url.getRawFragment() == null;
    }
}

