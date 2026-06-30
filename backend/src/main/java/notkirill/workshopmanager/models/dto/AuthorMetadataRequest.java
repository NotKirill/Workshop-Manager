package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.ModspaceType;

public record AuthorMetadataRequest(
        @NotNull ModspaceType sourceModspace,
        @NotNull String originalId,
        @NotNull String name
) {
}
