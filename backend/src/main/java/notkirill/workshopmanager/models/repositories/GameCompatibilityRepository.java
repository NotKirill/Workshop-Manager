package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.GameCompatibility;
import notkirill.workshopmanager.models.idClasses.GameCompatibilityComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameCompatibilityRepository extends JpaRepository<GameCompatibility, GameCompatibilityComplexKey> {
    Optional<GameCompatibility> findBySourceGameId(long sourceGameId);

    Optional<GameCompatibility> findByTagetGameId(long tagetGameId);
}