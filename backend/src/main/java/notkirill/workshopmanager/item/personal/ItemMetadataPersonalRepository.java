package notkirill.workshopmanager.item.personal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemMetadataPersonalRepository extends JpaRepository<ItemMetadataPersonal, Long>,
        JpaSpecificationExecutor<ItemMetadataPersonal> {
    List<ItemMetadataPersonal> findByInternalMetadataId(long internalMetadataId);

    Optional<ItemMetadataPersonal> findByInternalMetadataIdAndUserId(long internalMetadataId, long userId);

    Optional<ItemMetadataPersonal> findByIdAndUserId(long id, long userId);

    List<ItemMetadataPersonal> findByUserIdAndIdIn(long userId, Collection<Long> ids);

    List<ItemMetadataPersonal> findByUserIdAndInternalMetadataIdIn(long userId, Collection<Long> internalMetadataIds);

    List<ItemMetadataPersonal> findByUserId(long userId);

    boolean existsByInternalMetadataId(long internalMetadataId);

    long countByUserId(long userId);

    boolean existsByInternalMetadataIdAndAutoBackupTrue(long internalMetadataId);

    long countByInternalMetadataId(long internalMetadataId);

    @Transactional
    @Modifying
    @Query("""
            UPDATE ItemMetadataPersonal p SET p.dependenciesNeedReview = false
            WHERE p.id IN :ids AND p.dependenciesNeedReview IS NULL
            """)
    int trackDependencies(Collection<Long> ids);

    @Transactional
    @Modifying
    @Query("""
            UPDATE ItemMetadataPersonal p SET p.dependenciesNeedReview = true
            WHERE p.internalMetadataId = :internalId AND p.dependenciesNeedReview IS NOT NULL
            """)
    int requestDependencyReview(long internalId);
}
