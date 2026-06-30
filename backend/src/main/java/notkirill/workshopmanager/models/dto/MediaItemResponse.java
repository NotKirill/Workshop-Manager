package notkirill.workshopmanager.models.dto;

import notkirill.workshopmanager.models.enums.MediaType;

import java.time.OffsetDateTime;

public record MediaItemResponse(
        Long id,
        MediaType type,
        OffsetDateTime timeUploaded,
        Long ownerUserId
) {
}
