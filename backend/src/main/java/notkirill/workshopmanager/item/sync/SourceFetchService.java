package notkirill.workshopmanager.item.sync;

import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import notkirill.workshopmanager.item.shared.ProjectionData;
import notkirill.workshopmanager.modspace.ModspaceRegistry;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.SourceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SourceFetchService {
    private final ModspaceRegistry modspaceRegistry;
    private final ItemInternalService internalService;
    private final ItemProjectionService projectionService;

    @Autowired
    public SourceFetchService(ModspaceRegistry modspaceRegistry, ItemInternalService internalService,
                              ItemProjectionService projectionService) {
        this.modspaceRegistry = modspaceRegistry;
        this.internalService = internalService;
        this.projectionService = projectionService;
    }

    public List<SyncOutcome> sync(ModspaceType modspace, Collection<String> originalIds) {
        return modspaceRegistry.get(modspace).sync(originalIds, this::store);
    }

    private SyncOutcome store(SourceItem source) {
        if (!source.available()) {
            ItemMetadataInternal internal = internalService.findOrCreate(
                    source.modspace(), GameMetadata.UNKNOWN_ID, source.originalId());
            internalService.markUnavailable(internal);
            return new SyncOutcome(source.originalId(), SyncStatus.UNAVAILABLE, internal.getId(), false, false);
        }
        ItemMetadataInternal internal = internalService.findOrCreate(
                source.modspace(), source.gameId(), source.originalId());
        internalService.assignGame(internal, source.gameId());
        long internalId = internal.getId();
        projectionService.upsert(
                internalId, new ProjectionData(
                        source.title(),
                        source.description(),
                        source.authorId(),
                        source.fileSize(),
                        source.timeCreated()
                )
        );
        boolean sourceChanged = internalService.markSynced(internal, source.timeUpdated());
        return new SyncOutcome(
                source.originalId(), SyncStatus.SYNCED, internalId, sourceChanged,
                source.dependenciesChanged()
        );
    }
}
