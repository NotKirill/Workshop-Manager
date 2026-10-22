package notkirill.workshopmanager.item.organization.tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    List<UserTag> findByUserIdOrderByDisplayNameAsc(long userId);

    List<UserTag> findByUserIdAndGameIdOrderByDisplayNameAsc(long userId, long gameId);

    Optional<UserTag> findByIdAndUserId(long id, long userId);

    List<UserTag> findByUserIdAndIdIn(long userId, Collection<Long> ids);

    Optional<UserTag> findByUserIdAndGameIdAndDisplayNameIgnoreCase(long userId, long gameId, String displayName);

    List<UserTag> findByUserId(long userId);
}
