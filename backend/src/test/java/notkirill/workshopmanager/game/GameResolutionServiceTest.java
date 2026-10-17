package notkirill.workshopmanager.game;

import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.game.models.GameToModspaceId;
import notkirill.workshopmanager.game.repositories.GameMetadataRepository;
import notkirill.workshopmanager.game.repositories.GameToModspaceIdRepository;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.source.MediaSourceSyncService;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameResolutionServiceTest {
    private static final Instant NOW_INSTANT = Instant.parse("2025-01-15T15:15:00Z");

    private GameMetadataRepository gameRepository;
    private GameToModspaceIdRepository mappingRepository;
    private MediaSourceSyncService mediaSourceSyncService;
    private GameResolutionService service;

    @BeforeEach
    void setUp() {
        gameRepository = mock(GameMetadataRepository.class);
        mappingRepository = mock(GameToModspaceIdRepository.class);
        when(gameRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mappingRepository.findBySourceModspaceAndExternalId(any(), any())).thenReturn(Optional.empty());
        mediaSourceSyncService = mock(MediaSourceSyncService.class);
        service = new GameResolutionService(
                gameRepository, mappingRepository, mock(DatabaseLocks.class),
                Clock.fixed(NOW_INSTANT, ZoneOffset.UTC), mediaSourceSyncService
        );
    }

    @Test
    void gameSeenWithoutANameGetsThePlaceholderAndIsQueued() {
        service.resolve(ModspaceType.STEAM, "4000", null, "Steam App 4000");

        assertThat(savedGame().getName()).isEqualTo("Steam App 4000");
        assertThat(savedMapping().getExternalId()).isEqualTo("4000");
        assertThat(savedGame().getNameRequestedAt()).isEqualTo(OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC));
    }

    @Test
    void gameSeenWithANameIsNotQueued() {
        service.resolve(ModspaceType.STEAM, "4000", " Garry's Mod ", "Steam App 4000");

        assertThat(savedGame().getName()).isEqualTo("Garry's Mod");
        assertThat(savedGame().getNameRequestedAt()).isNull();
    }

    @Test
    void newGameRequestsItsCover() {
        long gameId = service.resolve(ModspaceType.STEAM, "4000", "Garry's Mod", "Steam App 4000");

        verify(mediaSourceSyncService).onSourceSynced(EntityType.GAME, gameId, ModspaceType.STEAM, "4000", true);
    }

    @Test
    void knownGameIsReturnedAsIs() {
        when(mappingRepository.findBySourceModspaceAndExternalId(ModspaceType.STEAM, "4000"))
                .thenReturn(Optional.of(new GameToModspaceId(7, ModspaceType.STEAM, "4000")));

        assertThat(service.resolve(ModspaceType.STEAM, "4000", null, "Steam App 4000")).isEqualTo(7);
        verify(gameRepository, never()).save(any());
        verify(mappingRepository, never()).save(any());
        verifyNoInteractions(mediaSourceSyncService);
    }

    private GameMetadata savedGame() {
        ArgumentCaptor<GameMetadata> captor = ArgumentCaptor.forClass(GameMetadata.class);
        verify(gameRepository).save(captor.capture());
        return captor.getValue();
    }

    private GameToModspaceId savedMapping() {
        ArgumentCaptor<GameToModspaceId> captor = ArgumentCaptor.forClass(GameToModspaceId.class);
        verify(mappingRepository).save(captor.capture());
        return captor.getValue();
    }
}
