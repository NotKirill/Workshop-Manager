package notkirill.workshopmanager.modspace;

import notkirill.workshopmanager.game.models.GameMetadata;

import java.time.OffsetDateTime;

public record SourceItem(
        ModspaceType modspace,
        String originalId,
        boolean available,
        boolean hidden,
        long gameId,
        String title,
        String description,
        Long authorId,
        Long fileSize,
        OffsetDateTime timeCreated,
        OffsetDateTime timeUpdated,
        boolean dependenciesChanged,
        boolean mediaChanged
) {
    public static SourceItem unavailable(ModspaceType modspace, String originalId) {
        return new SourceItem(
                modspace, originalId, false, false, GameMetadata.UNKNOWN_ID, null, null, null, null, null, null,
                false, false
        );
    }
}
