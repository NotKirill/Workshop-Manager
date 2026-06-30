package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    Optional<UserTag> findByGameId(long gameId);
}