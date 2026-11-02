package notkirill.workshopmanager.item.library;

import notkirill.workshopmanager.item.organization.rating.RatingService;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.item.shared.ItemMetadataProjection;
import notkirill.workshopmanager.item.shared.ItemProjectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserItemLoader {
    private final PersonalItemService personalService;
    private final ItemInternalService internalService;
    private final ItemProjectionService projectionService;
    private final RatingService ratingService;

    @Autowired
    public UserItemLoader(PersonalItemService personalService, ItemInternalService internalService,
                          ItemProjectionService projectionService, RatingService ratingService) {
        this.personalService = personalService;
        this.internalService = internalService;
        this.projectionService = projectionService;
        this.ratingService = ratingService;
    }

    @Transactional(readOnly = true)
    public Optional<UserItem> find(long userId, long personalId) {
        return personalService.findOwned(userId, personalId)
                .map(personal -> load(List.of(personal)).getFirst());
    }

    @Transactional(readOnly = true)
    public List<UserItem> findAll(long userId, Collection<Long> personalIds) {
        if (personalIds.isEmpty()) {
            return List.of();
        }
        return load(personalService.findOwnedAll(userId, personalIds));
    }

    @Transactional(readOnly = true)
    public List<UserItem> load(List<ItemMetadataPersonal> personals) {
        if (personals.isEmpty()) {
            return List.of();
        }
        Set<Long> internalIds = personals.stream().map(ItemMetadataPersonal::getInternalMetadataId)
                .collect(Collectors.toSet());
        Map<Long, ItemMetadataInternal> internals = internalService.findAll(internalIds);
        Map<Long, ItemMetadataProjection> projections = projectionService.findAll(internalIds);
        Map<Long, Short> ratings = ratingService.scoresOf(personals.stream().map(ItemMetadataPersonal::getId).toList());

        List<UserItem> items = new ArrayList<>();
        for (ItemMetadataPersonal personal : personals) {
            items.add(new UserItem(
                    personal,
                    internals.get(personal.getInternalMetadataId()),
                    projections.get(personal.getInternalMetadataId()),
                    ratings.get(personal.getId())
            ));
        }
        return items;
    }
}
