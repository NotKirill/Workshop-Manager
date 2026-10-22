package notkirill.workshopmanager.item.organization.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserItemRelationRepository extends JpaRepository<UserItemRelation, UserItemRelation.CompositeId> {
    List<UserItemRelation> findBySourceItemPersonalMetadataId(long sourceItemPersonalMetadataId);

    List<UserItemRelation> findByTargetItemPersonalMetadataId(long targetItemPersonalMetadataId);

    List<UserItemRelation> findBySourceItemPersonalMetadataIdAndTargetItemPersonalMetadataId(
            long sourceItemPersonalMetadataId, long targetItemPersonalMetadataId);

    @Modifying
    @Query("""
            DELETE FROM UserItemRelation r
            WHERE r.sourceItemPersonalMetadataId IN :personalIds OR r.targetItemPersonalMetadataId IN :personalIds
            """)
    int deleteTouching(Collection<Long> personalIds);
}
