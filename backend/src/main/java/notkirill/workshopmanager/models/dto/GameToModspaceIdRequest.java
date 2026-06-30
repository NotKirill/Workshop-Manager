package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.ModspaceType;


public record GameToModspaceIdRequest(
        @NotNull Long gameId,
        @NotNull ModspaceType sourceModspace,
        @NotNull String externalId
) {
}
