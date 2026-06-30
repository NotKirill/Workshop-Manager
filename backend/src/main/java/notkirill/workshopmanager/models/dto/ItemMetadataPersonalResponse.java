package notkirill.workshopmanager.models.dto;

public record ItemMetadataPersonalResponse(
        Long id,
        Long userId,
        Long internalMetadataId,
        String title,
        String description,
        String userCommentary,
        Boolean metadataOnly,
        Boolean contentImport
) {
}
