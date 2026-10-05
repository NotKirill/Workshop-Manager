package notkirill.workshopmanager.game.repositories;

import notkirill.workshopmanager.game.models.GameCompatibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GameCompatibilityRepository extends JpaRepository<GameCompatibility, GameCompatibility.CompositeId> {
    List<GameCompatibility> findBySourceGameIdIn(Collection<Long> sourceGameIds);

    List<GameCompatibility> findByTargetGameId(long targetGameId);
}