package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.GameCompatibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameCompatibilityRepository extends JpaRepository<GameCompatibility, GameCompatibility.CompositeId> {
    List<GameCompatibility> findBySourceGameId(long sourceGameId);

    List<GameCompatibility> findByTargetGameId(long targetGameId);
}