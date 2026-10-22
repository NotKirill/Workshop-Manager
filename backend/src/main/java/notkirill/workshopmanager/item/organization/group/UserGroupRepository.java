package notkirill.workshopmanager.item.organization.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByUserIdOrderByNameAsc(long userId);

    List<UserGroup> findByUserIdAndGameIdOrderByNameAsc(long userId, long gameId);

    Optional<UserGroup> findByIdAndUserId(long id, long userId);

    List<UserGroup> findByUserIdAndIdIn(long userId, Collection<Long> ids);

    Optional<UserGroup> findByUserIdAndGameIdAndNameIgnoreCase(long userId, long gameId, String name);

    List<UserGroup> findByUserIdAndNameIgnoreCase(long userId, String name);

    List<UserGroup> findByUserId(long userId);
}
