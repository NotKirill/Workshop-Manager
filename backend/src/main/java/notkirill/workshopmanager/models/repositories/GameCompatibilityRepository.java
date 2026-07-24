package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.GameCompatibility;
import notkirill.workshopmanager.models.idClasses.GameCompatibilityComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameCompatibilityRepository extends JpaRepository<GameCompatibility, GameCompatibilityComplexKey> {
    List<GameCompatibility> findBySourceGameId(long sourceGameId);

    List<GameCompatibility> findByTagetGameId(long tagetGameId);
}