package notkirill.workshopmanager.item.personal;

import notkirill.workshopmanager.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalItemService {
    private final ItemMetadataPersonalRepository repository;

    @Autowired
    public PersonalItemService(ItemMetadataPersonalRepository repository) {
        this.repository = repository;
    }

    public ItemMetadataPersonal requireOwned(long userId, long personalId) {
        return repository.findByIdAndUserId(personalId, userId)
                .orElseThrow(() -> new NotFoundException("ITEM_NOT_FOUND", "Item not found"));
    }

    public List<ItemMetadataPersonal> requireOwnedAll(long userId, Collection<Long> personalIds) {
        HashSet<Long> unique = new HashSet<>(personalIds);
        List<ItemMetadataPersonal> found = repository.findByUserIdAndIdIn(userId, unique);
        if (found.size() != unique.size()) {
            throw new NotFoundException("ITEM_NOT_FOUND", "One or more items were not found");
        }
        return found;
    }

    public Optional<ItemMetadataPersonal> findOwned(long userId, long personalId) {
        return repository.findByIdAndUserId(personalId, userId);
    }

    public List<ItemMetadataPersonal> findOwnedAll(long userId, Collection<Long> personalIds) {
        return repository.findByUserIdAndIdIn(userId, personalIds);
    }

    public Optional<ItemMetadataPersonal> findOfInternal(long userId, long internalId) {
        return repository.findByInternalMetadataIdAndUserId(internalId, userId);
    }

    public List<ItemMetadataPersonal> findOfInternals(long userId, Collection<Long> internalIds) {
        return repository.findByUserIdAndInternalMetadataIdIn(userId, internalIds);
    }

    public List<ItemMetadataPersonal> findAllOfUser(long userId) {
        return repository.findByUserId(userId);
    }

    public Page<ItemMetadataPersonal> search(Specification<ItemMetadataPersonal> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    public long countOfUser(long userId) {
        return repository.countByUserId(userId);
    }

    public boolean isReferenced(long internalId) {
        return repository.existsByInternalMetadataId(internalId);
    }

    public boolean hasAutoBackupUsers(long internalId) {
        return repository.existsByInternalMetadataIdAndAutoBackupTrue(internalId);
    }

    public boolean isOnlyUser(long internalId) {
        return repository.countByInternalMetadataId(internalId) <= 1;
    }

    public ItemMetadataPersonal save(ItemMetadataPersonal personal) {
        return repository.save(personal);
    }

    public void saveAll(Collection<ItemMetadataPersonal> personals) {
        repository.saveAll(personals);
    }

    public void deleteAll(Collection<ItemMetadataPersonal> personals) {
        repository.deleteAll(personals);
    }

    public void trackDependencies(Collection<Long> personalIds) {
        repository.trackDependencies(personalIds);
    }

    public void requestDependencyReview(long internalId) {
        repository.requestDependencyReview(internalId);
    }
}
