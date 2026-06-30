package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.AuthorMetadata;
import notkirill.workshopmanager.models.enums.ModspaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorMetadataRepository extends JpaRepository<AuthorMetadata, Long> {
    Optional<AuthorMetadata> findBySourceModspaceAndOriginalId(ModspaceType sourceModspace, String originalId);
}