package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.StoringStrategy;

public record ItemMetadataPersonalResponse(
        Long id,
        Long userId,
        Long internalMetadataId,
        String title,
        String description,
        String userCommentary,
        StoringStrategy storingMode
) {
}
