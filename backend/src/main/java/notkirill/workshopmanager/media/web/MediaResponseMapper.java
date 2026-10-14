package notkirill.workshopmanager.media.web;

import notkirill.workshopmanager.media.MediaEntry;
import notkirill.workshopmanager.media.MediaItemDetails;
import notkirill.workshopmanager.media.VisibleLink;
import org.springframework.stereotype.Component;

@Component
public class MediaResponseMapper {
    public MediaResponse toResponse(MediaEntry media) {
        VisibleLink entry = media.link();
        MediaItemDetails item = media.item();
        return new MediaResponse(
                entry.link().getId(),
                item.id(),
                item.type(),
                item.url(),
                item.provider(),
                item.externalId(),
                item.fileName(),
                entry.link().getPurpose(),
                entry.sortOrder(),
                entry.link().getOwnerUserId() != null,
                entry.hidden()
        );
    }
}
