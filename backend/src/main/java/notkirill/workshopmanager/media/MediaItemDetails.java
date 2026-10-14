package notkirill.workshopmanager.media;

import notkirill.workshopmanager.media.enums.MediaProvider;
import notkirill.workshopmanager.media.enums.MediaType;

public record MediaItemDetails(
        long id,
        MediaType type,
        String url,
        MediaProvider provider,
        String externalId,
        String fileName
) {
}
