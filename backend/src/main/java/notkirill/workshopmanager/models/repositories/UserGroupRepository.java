package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByUserId(long userId);

    Optional<UserGroup> findByGameId(long gameId);
}