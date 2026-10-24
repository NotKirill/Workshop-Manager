package notkirill.workshopmanager.item.lifecycle;

import notkirill.workshopmanager.exception.ConflictException;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataInternalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemLifecycleService {
    private final ItemMetadataInternalRepository internalRepository;
    private final ItemCleanupRepository cleanupRepository;
    private final PersonalItemService personalService;
    private final Clock clock;

    @Autowired
    public ItemLifecycleService(ItemMetadataInternalRepository internalRepository,
                                ItemCleanupRepository cleanupRepository, PersonalItemService personalService,
                                Clock clock) {
        this.internalRepository = internalRepository;
        this.cleanupRepository = cleanupRepository;
        this.personalService = personalService;
        this.clock = clock;
    }

    @Transactional
    public void onPersonalRemoved(long internalId) {
        internalRepository.findById(internalId).ifPresent(internal -> {
            if (internal.getLifecycleState() == ItemLifecycleState.ACTIVE
                    && !personalService.isReferenced(internalId)) {
                internal.setLifecycleState(ItemLifecycleState.ORPHANED);
                internalRepository.save(internal);
                cleanupRepository.save(new ItemCleanup(internalId, OffsetDateTime.now(clock)));
            }
        });
    }

    @Transactional
    public void prepareForRegistration(long internalId) {
        ItemMetadataInternal internal = internalRepository.findByIdForUpdate(internalId)
                .orElseThrow(() -> new ConflictException(
                        "ITEM_BEING_DELETED",
                        "This item was just removed, please retry"
                ));
        switch (internal.getLifecycleState()) {
            case ACTIVE -> {
                // nothing to do
            }
            case ORPHANED, FAILED -> revive(internal);
            case DELETING -> throw new ConflictException(
                    "ITEM_BEING_DELETED",
                    "This item is being cleaned up right now, please retry in a minute"
            );
        }
    }

    @Transactional
    public void revive(ItemMetadataInternal internal) {
        internal.setLifecycleState(ItemLifecycleState.ACTIVE);
        internalRepository.save(internal);
        cleanupRepository.deleteById(internal.getId());
    }

    @Transactional
    public List<Long> claimForDeletion(Duration gracePeriod, int limit) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        List<ItemMetadataInternal> candidates = internalRepository.claimCleanupCandidates(
                now.minus(gracePeriod), now,
                limit
        );
        List<Long> ids = new ArrayList<>();
        for (ItemMetadataInternal internal : candidates) {
            if (personalService.isReferenced(internal.getId())) {
                revive(internal);
            } else {
                internal.setLifecycleState(ItemLifecycleState.DELETING);
                internalRepository.save(internal);
                ids.add(internal.getId());
            }
        }
        return ids;
    }

    @Transactional
    public void deleteIdentity(long internalId) {
        internalRepository.findById(internalId).ifPresent(internal -> {
            if (internal.getLifecycleState() == ItemLifecycleState.DELETING
                    && !personalService.isReferenced(internalId)) {
                internalRepository.delete(internal);
                cleanupRepository.deleteById(internalId);
            }
        });
    }

    @Transactional
    public void markFailed(long internalId) {
        internalRepository.findById(internalId).ifPresent(internal -> {
            internal.setLifecycleState(ItemLifecycleState.FAILED);
            internalRepository.save(internal);
        });
    }

    @Transactional
    public void retryFailed(long internalId) {
        internalRepository.findById(internalId).ifPresent(internal -> {
            if (internal.getLifecycleState() != ItemLifecycleState.FAILED) {
                return;
            }
            internal.setLifecycleState(ItemLifecycleState.DELETING);
            internalRepository.save(internal);
            cleanupRepository.findById(internalId).ifPresent(cleanup -> {
                cleanup.setAttempts((short) 0);
                cleanup.setNextAttemptAt(null);
                cleanupRepository.save(cleanup);
            });
        });
    }
}
