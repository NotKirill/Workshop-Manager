package notkirill.workshopmanager.modspace.steam;

import notkirill.workshopmanager.modspace.steam.models.SteamVisibility;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;

public record SteamPublishedFile(
        String publishedFileId,
        int result,
        String creator,
        long creatorAppId,
        long consumerAppId,
        Long fileSize,
        String previewUrl,
        String title,
        String description,
        OffsetDateTime timeCreated,
        OffsetDateTime timeUpdated,
        short visibility,
        int subscriptions,
        int favorited,
        int lifetimeSubscriptions,
        int lifetimeFavorited,
        List<Tag> tags,
        Details details
) {
    public static final int RESULT_OK = 1;
    public static final int RESULT_FILE_NOT_FOUND = 9;

    public static SteamPublishedFile unavailable(String publishedFileId, int result) {
        return new SteamPublishedFile(
                publishedFileId, result, null, 0, 0, null, null, null, null, null, null,
                (short) 0, 0, 0, 0, 0, List.of(), null
        );
    }

    public boolean isAvailable() {
        return result == RESULT_OK;
    }

    public boolean isHidden() {
        return visibility != SteamVisibility.PUBLIC.code();
    }

    public record Tag(String key, String displayName) {
    }

    public record Details(
            String appName,
            String shortDescription,
            String previewFileSize,
            short numChildren,
            JsonNode previews,
            JsonNode children,
            JsonNode kvtags,
            Votes votes,
            boolean maybeInappropriateSex,
            boolean maybeInappropriateViolence,
            short[] contentDescriptorIds
    ) {
    }

    public record Votes(float score, int votesUp, int votesDown) {
    }
}
