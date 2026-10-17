package notkirill.workshopmanager.item.registration;

import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.exception.ExternalServiceException;
import notkirill.workshopmanager.game.GameService;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import notkirill.workshopmanager.item.sync.SourceFetchService;
import notkirill.workshopmanager.item.sync.SyncOutcome;
import notkirill.workshopmanager.item.sync.SyncStatus;
import notkirill.workshopmanager.item.web.dto.ExternalItemRegisteringRequest;
import notkirill.workshopmanager.modspace.ModspaceAdapter;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemRegistrationServiceTest {
    private static final long USER_ID = 1;

    private ItemInternalService internalService;
    private ModspaceAdapter steam;
    private SourceFetchService sourceFetchService;
    private ItemRegistrationService service;

    @BeforeEach
    void setUp() {
        internalService = mock(ItemInternalService.class);
        steam = mock(ModspaceAdapter.class);
        sourceFetchService = mock(SourceFetchService.class);
        when(steam.type()).thenReturn(ModspaceType.STEAM);
        when(steam.isValidOriginalId(any())).thenReturn(true);
        when(internalService.find(any(), any())).thenReturn(Optional.empty());
        when(internalService.find(anyLong())).thenAnswer(invocation ->
                                                                 Optional.of(
                                                                         new ItemMetadataInternal(
                                                                                 ModspaceType.STEAM, 1,
                                                                                 "fetched"
                                                                         )));

        PersonalItemService personalService = mock(PersonalItemService.class);
        when(personalService.findOfInternal(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(personalService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ItemProjectionService projectionService = mock(ItemProjectionService.class);
        when(projectionService.find(anyLong())).thenReturn(Optional.empty());

        service = new ItemRegistrationService(
                new ModspaceRegistry(List.of(steam)), sourceFetchService, internalService, projectionService,
                personalService, mock(ContentJobService.class), mock(GameService.class),
                mock(PlatformTransactionManager.class)
        );
    }

    @Test
    void knownActiveItemIsAttachedWithoutAskingTheModspace() {
        known("100", 7, ItemLifecycleState.ACTIVE);

        List<RegistrationResult> results = register("100");

        assertThat(results).extracting(RegistrationResult::status)
                .containsExactly(RegistrationResult.Status.REGISTERED);
        assertThat(results.getFirst().internalId()).isEqualTo(7);
        verify(sourceFetchService, never()).sync(any(), any());
    }

    @Test
    void unknownAndOrphanedItemsAreFetched() {
        known("100", 7, ItemLifecycleState.ACTIVE);
        known("200", 8, ItemLifecycleState.ORPHANED);
        when(sourceFetchService.sync(eq(ModspaceType.STEAM), any())).thenReturn(List.of(
                new SyncOutcome("200", SyncStatus.SYNCED, 8L, false, false),
                new SyncOutcome("300", SyncStatus.SYNCED, 9L, false, false),
                new SyncOutcome("400", SyncStatus.UNAVAILABLE, 10L, false, false)
        ));

        List<RegistrationResult> results = register("100", "200", "300", "400");

        verify(sourceFetchService).sync(ModspaceType.STEAM, Set.of("200", "300", "400"));
        assertThat(results)
                .extracting(RegistrationResult::originalId, RegistrationResult::status)
                .containsExactly(
                        tuple("100", RegistrationResult.Status.REGISTERED),
                        tuple("200", RegistrationResult.Status.REGISTERED),
                        tuple("300", RegistrationResult.Status.REGISTERED),
                        tuple("400", RegistrationResult.Status.REGISTERED)
                );
    }

    @Test
    void failedFetchFailsOnlyTheFetchedItems() {
        known("100", 7, ItemLifecycleState.ACTIVE);
        when(sourceFetchService.sync(eq(ModspaceType.STEAM), any())).thenThrow(
                new ExternalServiceException("STEAM_API_ERROR", "Steam GetDetails request failed: HTTP 503", true));

        List<RegistrationResult> results = register("100", "300");

        assertThat(results)
                .extracting(RegistrationResult::originalId, RegistrationResult::status)
                .containsExactly(
                        tuple("100", RegistrationResult.Status.REGISTERED),
                        tuple("300", RegistrationResult.Status.FAILED)
                );
    }

    @Test
    void itemRemovedBeforeAttachingFailsOnlyItself() {
        known("100", 7, ItemLifecycleState.ACTIVE);
        when(sourceFetchService.sync(eq(ModspaceType.STEAM), any())).thenReturn(List.of(
                new SyncOutcome("300", SyncStatus.SYNCED, 9L, false, false)
        ));
        when(internalService.find(9L)).thenReturn(Optional.empty());

        List<RegistrationResult> results = register("100", "300");

        assertThat(results)
                .extracting(RegistrationResult::originalId, RegistrationResult::status)
                .containsExactly(
                        tuple("100", RegistrationResult.Status.REGISTERED),
                        tuple("300", RegistrationResult.Status.FAILED)
                );
    }

    private void known(String originalId, long internalId, ItemLifecycleState state) {
        ItemMetadataInternal internal = new ItemMetadataInternal(ModspaceType.STEAM, 1, originalId);
        internal.setId(internalId);
        internal.setLifecycleState(state);
        when(internalService.find(ModspaceType.STEAM, originalId)).thenReturn(Optional.of(internal));
        when(internalService.find(internalId)).thenReturn(Optional.of(internal));
    }

    private List<RegistrationResult> register(String... originalIds) {
        return service.registerExternal(
                USER_ID, new ExternalItemRegisteringRequest(
                        List.of(new ExternalItemRegisteringRequest.ModspaceItems(
                                ModspaceType.STEAM, List.of(originalIds))),
                        false, false
                )
        );
    }
}
