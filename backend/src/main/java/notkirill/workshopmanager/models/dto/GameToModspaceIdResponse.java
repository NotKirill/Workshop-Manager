package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.ModspaceType;


public record GameToModspaceIdResponse(
        Long gameId,
        ModspaceType sourceModspace,
        String externalId
) {
}
