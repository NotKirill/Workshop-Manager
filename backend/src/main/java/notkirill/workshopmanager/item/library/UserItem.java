package notkirill.workshopmanager.item.library;

import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;

import java.time.OffsetDateTime;

public record UserItem(
        ItemMetadataPersonal personal,
        ItemMetadataInternal internal,
        ItemMetadataProjection projection,
        Short rating
) {
    public long personalId() {
        return personal.getId();
    }

    public String title() {
        return personal.getTitle() != null ? personal.getTitle() : projection != null ? projection.getTitle() : null;
    }

    public String description() {
        if (personal.getDescription() != null) {
            return personal.getDescription();
        }
        return projection != null ? projection.getDescription() : null;
    }

    public boolean titleOverridden() {
        return personal.getTitle() != null;
    }

    public boolean descriptionOverridden() {
        return personal.getDescription() != null;
    }

    public ItemSourceStatus sourceStatus() {
        return internal.getSourceStatus();
    }

    public Long fileSize() {
        return projection != null ? projection.getFileSize() : null;
    }

    public OffsetDateTime sourceTimeCreated() {
        return projection != null ? projection.getSourceTimeCreated() : null;
    }

    public OffsetDateTime sourceTimeUpdated() {
        return internal.getTimeUpdated();
    }
}
