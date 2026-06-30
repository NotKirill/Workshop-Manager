package notkirill.workshopmanager.models.dto;

import tools.jackson.databind.JsonNode;

public record MediaFileResponse(
        Long mediaItemId,
        String sourceId,
        Integer fileSize,
        String fileName,
        String s3Key,
        JsonNode metadata
) {
}
