package notkirill.workshopmanager.author;

import notkirill.workshopmanager.author.refresh.AuthorRefreshProperties;
import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorServiceTest {
    private static final Instant NOW_INSTANT = Instant.parse("2025-01-15T15:15:00Z");
    private static final OffsetDateTime NOW = OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC);

    private AuthorMetadataRepository repository;
    private AuthorService service;

    @BeforeEach
    void setUp() {
        repository = mock(AuthorMetadataRepository.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        service = new AuthorService(repository, mock(DatabaseLocks.class),
                new AuthorRefreshProperties(null, null, Duration.ofDays(7), null),
                Clock.fixed(NOW_INSTANT, ZoneOffset.UTC));
    }

    @Test
    void newAuthorWithFetchedNameIsFresh() {
        AuthorMetadata author = seen(null, "Creator", true);

        assertThat(author.getName()).isEqualTo("Creator");
        assertThat(author.getTimeSynced()).isEqualTo(NOW);
        assertThat(author.getRefreshRequestedAt()).isNull();
    }

    @Test
    void staleAuthorIsQueuedOnce() {
        AuthorMetadata stale = existing(NOW.minusDays(8), null);

        seen(stale, null, true);
        assertThat(stale.getRefreshRequestedAt()).isEqualTo(NOW);

        OffsetDateTime firstMark = stale.getRefreshRequestedAt();
        seen(stale, null, true);
        assertThat(stale.getRefreshRequestedAt()).isEqualTo(firstMark);
    }

    @Test
    void freshAuthorIsNotQueued() {
        AuthorMetadata fresh = existing(NOW.minusDays(1), null);

        seen(fresh, null, true);

        assertThat(fresh.getRefreshRequestedAt()).isNull();
    }

    @Test
    void nothingIsQueuedWhenProfilesCannotBeFetched() {
        AuthorMetadata neverSynced = existing(null, null);

        seen(neverSynced, null, false);

        assertThat(neverSynced.getRefreshRequestedAt()).isNull();
    }

    @Test
    void appliedProfileLeavesTheQueue() {
        AuthorMetadata queued = existing(NOW.minusDays(9), NOW.minusHours(2));

        service.applyProfile(queued, null, NOW);

        assertThat(queued.getName()).isEqualTo("76561197960287930");
        assertThat(queued.getTimeSynced()).isEqualTo(NOW);
        assertThat(queued.getRefreshRequestedAt()).isNull();
    }

    private AuthorMetadata existing(OffsetDateTime timeSynced, OffsetDateTime refreshRequestedAt) {
        AuthorMetadata author = new AuthorMetadata(ModspaceType.STEAM, "76561197960287930", "76561197960287930");
        author.setTimeSynced(timeSynced);
        author.setRefreshRequestedAt(refreshRequestedAt);
        return author;
    }

    private AuthorMetadata seen(AuthorMetadata existing, String fetchedName, boolean refreshable) {
        when(repository.findBySourceModspaceAndOriginalId(ModspaceType.STEAM, "76561197960287930"))
                .thenReturn(Optional.ofNullable(existing));
        AuthorMetadata[] saved = new AuthorMetadata[1];
        when(repository.save(any())).thenAnswer(invocation -> saved[0] = invocation.getArgument(0));
        service.recordSeen(ModspaceType.STEAM, "76561197960287930", fetchedName, refreshable);
        return saved[0];
    }
}
