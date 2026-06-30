package notkirill.workshopmanager.models.dto;

import java.time.OffsetDateTime;

public record ItemContentRevisionResponse(
        Long itemContentId,
        Long revision,
        String s3Key,
        String fileName,
        Integer fileSize,
        OffsetDateTime timeCreated
) {
}
