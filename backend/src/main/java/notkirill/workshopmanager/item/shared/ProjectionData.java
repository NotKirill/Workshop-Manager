package notkirill.workshopmanager.item.shared;

import java.time.OffsetDateTime;

public record ProjectionData(
        String title,
        String description,
        Long authorId,
        Long fileSize,
        OffsetDateTime sourceTimeCreated
) {
}
