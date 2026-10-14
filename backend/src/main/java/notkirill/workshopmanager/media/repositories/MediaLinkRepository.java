package notkirill.workshopmanager.media.repositories;

import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.models.MediaLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MediaLinkRepository extends JpaRepository<MediaLink, Long> {
    @Query("""
            SELECT l FROM MediaLink l
            WHERE l.entity = :entity AND l.entityId = :entityId AND (l.ownerUserId IS NULL OR l.ownerUserId = :userId)""")
    List<MediaLink> findVisible(EntityType entity, long entityId, long userId);

    @Query("""
            SELECT l FROM MediaLink l
            WHERE l.id = :linkId AND l.entity = :entity AND l.entityId = :entityId
              AND (l.ownerUserId IS NULL OR l.ownerUserId = :userId)""")
    Optional<MediaLink> findVisible(long linkId, EntityType entity, long entityId, long userId);

    List<MediaLink> findByEntityAndEntityIdAndOwnerUserIdAndPurpose(EntityType entity, long entityId,
                                                                    long ownerUserId, String purpose);

    List<MediaLink> findByEntityAndEntityIdInAndOwnerUserId(EntityType entity, Collection<Long> entityIds,
                                                            long ownerUserId);

    List<MediaLink> findByEntityAndEntityIdInAndOwnerUserIdIsNull(EntityType entity, Collection<Long> entityIds);

    boolean existsByMediaItemIdAndEntityAndEntityIdAndOwnerUserIdIsNull(long mediaItemId, EntityType entity,
                                                                        long entityId);

    @Query("""
            SELECT l FROM MediaLink l
            WHERE l.entity = :entity AND l.entityId IN :entityIds AND l.purpose = :purpose
              AND (l.ownerUserId IS NULL OR l.ownerUserId = :userId)""")
    List<MediaLink> findVisibleWithPurpose(EntityType entity, Collection<Long> entityIds, String purpose, long userId);

    List<MediaLink> findByEntityAndEntityIdInAndPurposeAndOwnerUserIdIsNull(EntityType entity,
                                                                            Collection<Long> entityIds,
                                                                            String purpose);

    List<MediaLink> findByEntityAndEntityIdAndOwnerUserIdIsNull(EntityType entity, long entityId);

    List<MediaLink> findByMediaItemIdAndOwnerUserIdIsNotNull(long mediaItemId);

    long countByMediaItemId(long mediaItemId);
}
