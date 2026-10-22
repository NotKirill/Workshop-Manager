package notkirill.workshopmanager.item.organization.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRatingRepository extends JpaRepository<UserRating, UserRating.CompositeId> {
    List<UserRating> findByUserId(long userId);

    Optional<UserRating> findByItemPersonalMetadataId(long itemPersonalMetadataId);

    List<UserRating> findByItemPersonalMetadataIdIn(Collection<Long> itemPersonalMetadataIds);

    @Modifying
    @Query("DELETE FROM UserRating r WHERE r.itemPersonalMetadataId IN :personalIds")
    int deleteByItemPersonalMetadataIdIn(Collection<Long> personalIds);
}
