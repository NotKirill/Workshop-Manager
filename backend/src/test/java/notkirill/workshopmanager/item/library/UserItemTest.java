package notkirill.workshopmanager.item.library;

import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserItemTest {

    @Test
    void personalValuesWinOverTheSource() {
        UserItem item = item(new ItemMetadataPersonal(1, 1, "My title", null, null, false),
                projection("Source title", "Source description"));

        assertThat(item.title()).isEqualTo("My title");
        assertThat(item.titleOverridden()).isTrue();
        assertThat(item.description()).isEqualTo("Source description");
        assertThat(item.descriptionOverridden()).isFalse();
    }

    @Test
    void itemWithoutSourceUsesPersonalValuesAndIsAvailable() {
        UserItem item = item(new ItemMetadataPersonal(1, 1, "Local", "Mine", null, false),
                null);

        assertThat(item.title()).isEqualTo("Local");
        assertThat(item.description()).isEqualTo("Mine");
        assertThat(item.sourceStatus()).isEqualTo(ItemSourceStatus.AVAILABLE);
        assertThat(item.fileSize()).isNull();
        assertThat(item.sourceTimeUpdated()).isNull();
    }

    @Test
    void syncStateComesFromTheInternalItem() {
        ItemMetadataInternal internal = new ItemMetadataInternal(ModspaceType.STEAM, 1, "105982362");
        internal.setSourceStatus(ItemSourceStatus.UNAVAILABLE);
        internal.setTimeUpdated(OffsetDateTime.parse("2026-03-01T00:00:00Z"));

        UserItem item = new UserItem(
                new ItemMetadataPersonal(1, 1, null, null, null, false), internal,
                projection("Source title", null), null);

        assertThat(item.sourceStatus()).isEqualTo(ItemSourceStatus.UNAVAILABLE);
        assertThat(item.sourceTimeUpdated()).isEqualTo(OffsetDateTime.parse("2026-03-01T00:00:00Z"));
        assertThat(item.title()).isEqualTo("Source title");
        assertThat(item.titleOverridden()).isFalse();
    }

    private static UserItem item(ItemMetadataPersonal personal, ItemMetadataProjection projection) {
        return new UserItem(personal, new ItemMetadataInternal(ModspaceType.STEAM, 1, "105982362"), projection, null);
    }

    private static ItemMetadataProjection projection(String title, String description) {
        ItemMetadataProjection projection = new ItemMetadataProjection(1);
        projection.setTitle(title);
        projection.setDescription(description);
        return projection;
    }
}
