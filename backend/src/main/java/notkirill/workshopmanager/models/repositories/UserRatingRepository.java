package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserRating;
import notkirill.workshopmanager.models.idClasses.UserRatingComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRatingRepository extends JpaRepository<UserRating, UserRatingComplexKey> {
    Optional<UserRating> findByUserId(long userId);

    Optional<UserRating> findByItemPersonalMetadataId(long itemPersonalMetadataId);
}