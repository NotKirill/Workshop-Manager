package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByUserId(long userId);

    List<UserGroup> findByGameId(long gameId);
}