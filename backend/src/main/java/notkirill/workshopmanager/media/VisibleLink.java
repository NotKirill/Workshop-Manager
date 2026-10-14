package notkirill.workshopmanager.media;

import notkirill.workshopmanager.media.models.MediaLink;

public record VisibleLink(MediaLink link, int sortOrder, boolean hidden) {
}
