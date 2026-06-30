package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.ModspaceType;

public record AuthorMetadataResponse(
        Long id,
        ModspaceType sourceModspace,
        String originalId,
        String name
) {
}
