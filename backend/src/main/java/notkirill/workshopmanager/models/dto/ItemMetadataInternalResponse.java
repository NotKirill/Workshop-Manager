package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.ModspaceType;

import java.time.OffsetDateTime;

public record ItemMetadataInternalResponse(
        Long id,
        ModspaceType sourceModspace,
        Long gameId,
        String originalId,
        OffsetDateTime timeRegistered,
        OffsetDateTime timeUpdated
) {
}
