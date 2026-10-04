package notkirill.workshopmanager.modspace.steam;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;


@ConfigurationProperties("application.modspace.steam")
@Validated
public record SteamProperties(
        @Name("web-api-key") String webApiKey,
        @Name("batch-min-size") @Positive @Max(MAX_IDS_PER_REQUEST) Integer batchMinSize,
        @Name("batch-max-size") @Positive @Max(MAX_IDS_PER_REQUEST) Integer batchMaxSize
) {
    /**
     * Fits every batched call: GetPlayerSummaries takes at most 100 ids (documented by Valve); keyed GetDetails sends
     * the ids in the URL, which Steam refuses above about 8 KB; the public GetPublishedFileDetails POST answered 1000 ids.
     */
    public static final int MAX_IDS_PER_REQUEST = 100;

    public SteamProperties {
        if (webApiKey != null && webApiKey.isBlank()) {
            webApiKey = null;
        }
        if (batchMaxSize == null) {
            batchMaxSize = MAX_IDS_PER_REQUEST;
        }
        if (batchMinSize == null) {
            batchMinSize = Math.min(70, batchMaxSize);
        }
        if (batchMinSize > batchMaxSize) {
            throw new IllegalArgumentException("batch-min-size cannot be larger than batch-max-size");
        }
    }

    public boolean hasWebApiKey() {
        return webApiKey != null;
    }
}
