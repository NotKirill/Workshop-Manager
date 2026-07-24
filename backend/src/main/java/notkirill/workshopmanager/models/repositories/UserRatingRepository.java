package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRatingRepository extends JpaRepository<UserRating, UserRating.CompositeId> {
    List<UserRating> findByUserId(long userId);

    Optional<UserRating> findByItemPersonalMetadataId(long itemPersonalMetadataId);
}