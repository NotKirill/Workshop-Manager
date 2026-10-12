package notkirill.workshopmanager.game.lookup;

import notkirill.workshopmanager.exception.ExternalServiceException;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.game.models.GameToModspaceId;
import notkirill.workshopmanager.game.repositories.GameMetadataRepository;
import notkirill.workshopmanager.game.repositories.GameToModspaceIdRepository;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class GameLookupServiceTest {
    private static final Instant NOW_INSTANT = Instant.parse("2025-01-15T15:15:00Z");
    private static final OffsetDateTime NOW = OffsetDateTime.ofInstant(NOW_INSTANT, ZoneOffset.UTC);

    private GameToModspaceIdRepository mappingRepository;
    private GameMetadataRepository gameRepository;
    private ModspaceAdapter steam;
    private GameLookupService service;

    @BeforeEach
    void setUp() {
        mappingRepository = mock(GameToModspaceIdRepository.class);
        gameRepository = mock(GameMetadataRepository.class);
        steam = mock(ModspaceAdapter.class);
        when(steam.type()).thenReturn(ModspaceType.STEAM);
        when(steam.supportsGameNames()).thenReturn(true);
        when(gameRepository.clearNameRequest(anyLong())).thenReturn(1);
        service = new GameLookupService(mappingRepository, gameRepository, new ModspaceRegistry(List.of(steam)),
                new GameLookupProperties(null, null, null), mock(PlatformTransactionManager.class),
                Clock.fixed(NOW_INSTANT, ZoneOffset.UTC));
    }

    @Test
    void fetchedNameReplacesThePlaceholder() {
        GameMetadata game = queuedGame(1, "Steam App 4000", "4000");
        when(steam.fetchGameName("4000")).thenReturn(Optional.of("Garry's Mod"));

        GameLookupService.LookupReport report = service.lookUpQueued();

        assertThat(report).isEqualTo(new GameLookupService.LookupReport(1, 0, 0));
        assertThat(game.getName()).isEqualTo("Garry's Mod");
        assertThat(game.getSlug()).isEqualTo("garry-s-mod");
        assertThat(game.getNameRequestedAt()).isNull();
        verify(gameRepository).clearNameRequest(1);
    }

    @Test
    void nextSourceIsAskedWhenTheFirstHasNoName() {
        GameMetadata game = queuedGame(1, "Steam App 4000", "4000", "4001");
        when(steam.fetchGameName("4000")).thenReturn(Optional.empty());
        when(steam.fetchGameName("4001")).thenReturn(Optional.of("Garry's Mod"));

        GameLookupService.LookupReport report = service.lookUpQueued();

        assertThat(report).isEqualTo(new GameLookupService.LookupReport(1, 0, 0));
        assertThat(game.getName()).isEqualTo("Garry's Mod");
        verify(gameRepository, times(1)).clearNameRequest(1);
    }

    @Test
    void firstNameFoundWinsAndTheOtherSourcesAreNotAsked() {
        queuedGame(1, "Steam App 4000", "4000", "4001");
        when(steam.fetchGameName("4000")).thenReturn(Optional.of("Garry's Mod"));

        service.lookUpQueued();

        verify(steam, never()).fetchGameName("4001");
    }

    @Test
    void gameUnknownToEverySourceKeepsThePlaceholderAndLeavesTheQueue() {
        GameMetadata game = queuedGame(1, "Steam App 4000", "4000");
        when(steam.fetchGameName("4000")).thenReturn(Optional.empty());

        GameLookupService.LookupReport report = service.lookUpQueued();

        assertThat(report).isEqualTo(new GameLookupService.LookupReport(0, 1, 0));
        assertThat(game.getName()).isEqualTo("Steam App 4000");
        verify(gameRepository).clearNameRequest(1);
        verify(gameRepository, never()).save(any());
    }

    @Test
    void failureRequeuesTheGameAndStopsTheModspaceUntilTheNextRun() {
        GameMetadata first = game(1, "Steam App 4000");
        GameMetadata second = game(2, "Steam App 4020");
        queue(first, second);
        mappings(1, "4000");
        mappings(2, "4020");
        when(steam.fetchGameName("4000")).thenThrow(
                new ExternalServiceException("STEAM_API_ERROR", "Steam appdetails request failed: HTTP 503", true));

        GameLookupService.LookupReport report = service.lookUpQueued();

        assertThat(report).isEqualTo(new GameLookupService.LookupReport(0, 0, 1));
        verify(gameRepository).requeueNameRequest(1, NOW);
        verify(steam, never()).fetchGameName("4020");
        verify(gameRepository, never()).clearNameRequest(anyLong());
    }

    @Test
    void gameRenamedMeanwhileIsNotOverwritten() {
        GameMetadata game = queuedGame(1, "Named by hand", "4000");
        when(steam.fetchGameName("4000")).thenReturn(Optional.of("Garry's Mod"));
        when(gameRepository.clearNameRequest(1)).thenReturn(0);

        GameLookupService.LookupReport report = service.lookUpQueued();

        assertThat(report).isEqualTo(new GameLookupService.LookupReport(0, 0, 0));
        assertThat(game.getName()).isEqualTo("Named by hand");
        verify(gameRepository, never()).save(any());
    }

    @Test
    void markStaysWhileNoSourceCanFetchNames() {
        queuedGame(1, "Steam App 4000", "4000");
        when(steam.supportsGameNames()).thenReturn(false);

        GameLookupService.LookupReport report = service.lookUpQueued();

        assertThat(report).isEqualTo(new GameLookupService.LookupReport(0, 0, 0));
        verify(steam, never()).fetchGameName(any());
        verify(gameRepository, never()).clearNameRequest(anyLong());
        verify(gameRepository, never()).requeueNameRequest(anyLong(), any());
    }

    private GameMetadata queuedGame(long id, String name, String... appIds) {
        GameMetadata game = game(id, name);
        queue(game);
        mappings(id, appIds);
        return game;
    }

    private void queue(GameMetadata... games) {
        when(gameRepository.findByNameRequestedAtIsNotNullOrderByNameRequestedAtAsc(any()))
                .thenReturn(List.of(games));
    }

    private void mappings(long gameId, String... appIds) {
        when(mappingRepository.findByGameId(gameId)).thenReturn(Arrays.stream(appIds)
                .map(appId -> new GameToModspaceId(gameId, ModspaceType.STEAM, appId))
                .toList());
    }

    private GameMetadata game(long id, String name) {
        GameMetadata game = new GameMetadata(name);
        game.setId(id);
        game.setNameRequestedAt(NOW.minusMinutes(5));
        when(gameRepository.findById(id)).thenReturn(Optional.of(game));
        when(gameRepository.save(game)).thenReturn(game);
        return game;
    }
}
