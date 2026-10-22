package notkirill.workshopmanager.item.organization.relation;

import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.item.enums.RelationType;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class RelationService {
    private final UserItemRelationRepository repository;
    private final PersonalItemService personalService;

    @Autowired
    public RelationService(UserItemRelationRepository repository, PersonalItemService personalService) {
        this.repository = repository;
        this.personalService = personalService;
    }

    @Transactional
    public void add(long userId, long sourceId, long targetId, RelationType relation) {
        if (sourceId == targetId) {
            throw new BadRequestException("INVALID_RELATION", "An item cannot be related to itself");
        }
        personalService.requireOwnedAll(userId, List.of(sourceId, targetId));
        link(sourceId, targetId, relation);
    }

    @Transactional
    public void remove(long userId, long sourceId, long targetId, RelationType relation) {
        personalService.requireOwned(userId, sourceId);
        repository.deleteById(new UserItemRelation.CompositeId(sourceId, targetId, relation));
    }

    @Transactional
    public void linkDependency(long userId, long sourceId, long targetId) {
        if (sourceId != targetId) {
            personalService.requireOwnedAll(userId, List.of(sourceId, targetId));
            link(sourceId, targetId, RelationType.DEPENDENCY);
        }
    }

    @Transactional
    public void unlinkDependency(long userId, long sourceId, long targetId) {
        personalService.requireOwned(userId, sourceId);
        repository.deleteById(new UserItemRelation.CompositeId(sourceId, targetId, RelationType.DEPENDENCY));
    }

    @Transactional
    public void deleteOfItems(Collection<Long> personalIds) {
        repository.deleteTouching(personalIds);
    }

    @Transactional(readOnly = true)
    public List<UserItemRelation> findOutgoing(long personalId) {
        return repository.findBySourceItemPersonalMetadataId(personalId);
    }

    @Transactional(readOnly = true)
    public List<UserItemRelation> findIncoming(long personalId) {
        return repository.findByTargetItemPersonalMetadataId(personalId);
    }

    private void link(long sourceId, long targetId, RelationType relation) {
        UserItemRelation.CompositeId id = new UserItemRelation.CompositeId(sourceId, targetId, relation);
        if (!repository.existsById(id)) {
            repository.save(new UserItemRelation(sourceId, targetId, relation));
        }
    }
}
