package notkirill.workshopmanager.models.dto;

public record ItemContentResponse(
        Long id,
        Long internalMetadataId,
        Integer referenceCount,
        String edition,
        String[] attributes,
        Short maxRevisions
) {
}
