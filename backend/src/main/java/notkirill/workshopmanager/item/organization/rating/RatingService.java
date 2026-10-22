package notkirill.workshopmanager.item.organization.rating;

import notkirill.workshopmanager.item.personal.PersonalItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final UserRatingRepository repository;
    private final PersonalItemService personalService;

    @Autowired
    public RatingService(UserRatingRepository repository, PersonalItemService personalService) {
        this.repository = repository;
        this.personalService = personalService;
    }

    @Transactional
    public void rate(long userId, long personalId, short score) {
        personalService.requireOwned(userId, personalId);
        UserRating rating = repository.findById(new UserRating.CompositeId(userId, personalId))
                .orElseGet(() -> new UserRating(userId, personalId, score));
        rating.setScore(score);
        repository.save(rating);
    }

    @Transactional
    public void clear(long userId, long personalId) {
        personalService.requireOwned(userId, personalId);
        repository.findById(new UserRating.CompositeId(userId, personalId)).ifPresent(repository::delete);
    }

    @Transactional(readOnly = true)
    public Map<Long, Short> scoresOf(Collection<Long> personalIds) {
        return repository.findByItemPersonalMetadataIdIn(personalIds).stream()
                .collect(Collectors.toMap(UserRating::getItemPersonalMetadataId, UserRating::getScore));
    }

    @Transactional
    public void deleteOfItems(Collection<Long> personalIds) {
        repository.deleteByItemPersonalMetadataIdIn(personalIds);
    }
}
