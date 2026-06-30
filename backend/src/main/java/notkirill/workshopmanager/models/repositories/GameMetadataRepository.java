package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.GameMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameMetadataRepository extends JpaRepository<GameMetadata, Long> {
}