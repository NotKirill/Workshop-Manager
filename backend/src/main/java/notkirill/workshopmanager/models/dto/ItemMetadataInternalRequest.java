package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.ModspaceType;

import java.time.OffsetDateTime;

public record ItemMetadataInternalRequest(
        @NotNull ModspaceType sourceModspace,
        @NotNull Long gameId,
        @NotNull String originalId,
        @NotNull OffsetDateTime timeUpdated
) {
}
