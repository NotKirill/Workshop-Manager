package notkirill.workshopmanager.author;

import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AuthorMetadataRepository extends JpaRepository<AuthorMetadata, Long> {
    Optional<AuthorMetadata> findBySourceModspaceAndOriginalId(ModspaceType sourceModspace, String originalId);

    List<AuthorMetadata> findBySourceModspaceAndOriginalIdIn(ModspaceType sourceModspace,
                                                             Collection<String> originalIds);

    List<AuthorMetadata> findByRefreshRequestedAtIsNotNullOrderByRefreshRequestedAtAsc(Pageable pageable);

    long countByRefreshRequestedAtIsNotNull();
}
