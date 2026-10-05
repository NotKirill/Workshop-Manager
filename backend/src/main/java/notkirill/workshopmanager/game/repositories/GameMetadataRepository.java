package notkirill.workshopmanager.game.repositories;

import notkirill.workshopmanager.game.models.GameMetadata;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface GameMetadataRepository extends JpaRepository<GameMetadata, Long> {
    Optional<GameMetadata> findFirstByNameIgnoreCase(String name);

    List<GameMetadata> findAllByOrderByNameAsc();

    List<GameMetadata> findByNameRequestedAtIsNotNullOrderByNameRequestedAtAsc(Pageable pageable);

    long countByNameRequestedAtIsNotNull();

    @Modifying
    @Query("UPDATE GameMetadata g SET g.nameRequestedAt = NULL WHERE g.id = :gameId "
            + "AND g.nameRequestedAt IS NOT NULL")
    int clearNameRequest(long gameId);

    @Modifying
    @Query("UPDATE GameMetadata g SET g.nameRequestedAt = :now WHERE g.id = :gameId "
            + "AND g.nameRequestedAt IS NOT NULL")
    int requeueNameRequest(long gameId, OffsetDateTime now);
}
