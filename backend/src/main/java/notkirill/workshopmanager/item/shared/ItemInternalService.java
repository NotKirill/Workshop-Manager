package notkirill.workshopmanager.item.shared;

import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.game.models.GameMetadata;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemInternalService {
    private final ItemMetadataInternalRepository repository;
    private final DatabaseLocks databaseLocks;
    private final Clock clock;

    @Autowired
    public ItemInternalService(ItemMetadataInternalRepository repository, DatabaseLocks databaseLocks, Clock clock) {
        this.repository = repository;
        this.databaseLocks = databaseLocks;
        this.clock = clock;
    }

    public Optional<ItemMetadataInternal> find(ModspaceType modspace, String originalId) {
        return repository.findFirstBySourceModspaceAndOriginalId(modspace, originalId);
    }

    public Optional<ItemMetadataInternal> find(long id) {
        return repository.findById(id);
    }

    public Map<Long, ItemMetadataInternal> findAll(Collection<Long> ids) {
        return repository.findAllById(ids).stream()
                .collect(Collectors.toMap(ItemMetadataInternal::getId, Function.identity()));
    }

    public List<ItemMetadataInternal> findSyncCandidates(ModspaceType modspace, OffsetDateTime syncedBefore,
                                                         int limit) {
        return repository.findBySyncState(
                modspace, ItemLifecycleState.ACTIVE, syncedBefore,
                PageRequest.of(0, limit)
        );
    }

    public long countByLifecycleState(ItemLifecycleState state) {
        return repository.countByLifecycleState(state);
    }

    public ItemMetadataInternal require(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("ITEM_NOT_FOUND", "Item not found"));
    }

    @Transactional
    public ItemMetadataInternal findOrCreate(ModspaceType modspace, long gameId, String originalId) {
        Optional<ItemMetadataInternal> existing = find(modspace, originalId);
        if (existing.isPresent()) {
            return existing.get();
        }
        databaseLocks.lock("internal:" + modspace + ":" + originalId);
        return find(modspace, originalId)
                .orElseGet(() -> repository.save(
                        new ItemMetadataInternal(modspace, gameId, originalId)));
    }

    @Transactional
    public ItemMetadataInternal createLocal(long gameId, String originalId) {
        return repository.save(new ItemMetadataInternal(ModspaceType.LOCAL, gameId, originalId));
    }

    @Transactional
    public void assignGame(ItemMetadataInternal internal, long gameId) {
        if (internal.getGameId() == GameMetadata.UNKNOWN_ID && gameId != GameMetadata.UNKNOWN_ID) {
            internal.setGameId(gameId);
            repository.save(internal);
        }
    }

    @Transactional
    public boolean markSynced(ItemMetadataInternal internal, OffsetDateTime sourceTimeUpdated) {
        boolean changed = internal.getTimeSynced() != null
                && !Objects.equals(internal.getTimeUpdated(), sourceTimeUpdated);
        internal.setTimeUpdated(sourceTimeUpdated);
        internal.setSourceStatus(ItemSourceStatus.AVAILABLE);
        internal.setTimeSynced(OffsetDateTime.now(clock));
        repository.save(internal);
        return changed;
    }

    @Transactional
    public void markUnavailable(ItemMetadataInternal internal) {
        internal.setSourceStatus(ItemSourceStatus.UNAVAILABLE);
        internal.setTimeSynced(OffsetDateTime.now(clock));
        repository.save(internal);
    }
}
