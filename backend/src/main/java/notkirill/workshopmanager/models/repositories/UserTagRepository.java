package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {
    List<UserTag> findByGameId(long gameId);
}