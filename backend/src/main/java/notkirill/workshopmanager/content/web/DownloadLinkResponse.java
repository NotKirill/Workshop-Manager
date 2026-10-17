package notkirill.workshopmanager.content.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DownloadLinkResponse(
        @JsonProperty("url") String url,
        @JsonProperty("expires_at") long expiresAt
) {
}
