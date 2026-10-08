package notkirill.workshopmanager.item.shared;

import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ItemInternalServiceTest {
    private static final Instant NOW_INSTANT = Instant.parse("2025-01-15T15:15:00Z");
    private static final OffsetDateTime NOW = OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC);
    private static final OffsetDateTime VERSION_1 = OffsetDateTime.parse("2024-03-01T00:00:00Z");
    private static final OffsetDateTime VERSION_2 = OffsetDateTime.parse("2024-04-01T00:00:00Z");

    private ItemInternalService service;
    private ItemMetadataInternal internal;

    @BeforeEach
    void setUp() {
        service = new ItemInternalService(
                mock(ItemMetadataInternalRepository.class), mock(DatabaseLocks.class),
                Clock.fixed(NOW_INSTANT, ZoneOffset.UTC)
        );
        internal = new ItemMetadataInternal(ModspaceType.STEAM, 1, "105982362");
    }

    @Test
    void firstSyncStoresTheVersionWithoutReportingAChange() {
        boolean changed = service.markSynced(internal, VERSION_1);

        assertThat(changed).isFalse();
        assertThat(internal.getTimeUpdated()).isEqualTo(VERSION_1);
        assertThat(internal.getTimeSynced()).isEqualTo(NOW);
    }

    @Test
    void sameVersionIsNoChange() {
        service.markSynced(internal, VERSION_1);

        assertThat(service.markSynced(internal, VERSION_1)).isFalse();
    }

    @Test
    void newVersionIsAChange() {
        service.markSynced(internal, VERSION_1);

        assertThat(service.markSynced(internal, VERSION_2)).isTrue();
        assertThat(internal.getTimeUpdated()).isEqualTo(VERSION_2);
    }

    @Test
    void unavailableItemKeepsItsVersionAndComesBackAvailable() {
        service.markSynced(internal, VERSION_1);

        service.markUnavailable(internal);

        assertThat(internal.getSourceStatus()).isEqualTo(ItemSourceStatus.UNAVAILABLE);
        assertThat(internal.getTimeUpdated()).isEqualTo(VERSION_1);
        assertThat(internal.getTimeSynced()).isEqualTo(NOW);

        assertThat(service.markSynced(internal, VERSION_1)).isFalse();
        assertThat(internal.getSourceStatus()).isEqualTo(ItemSourceStatus.AVAILABLE);
    }
}
