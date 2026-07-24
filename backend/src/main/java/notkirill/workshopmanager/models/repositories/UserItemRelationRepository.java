package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserItemRelation;
import notkirill.workshopmanager.models.idClasses.UserItemRelationComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRelationRepository extends JpaRepository<UserItemRelation, UserItemRelationComplexKey> {
    List<UserItemRelation> findBySourceItemPersonalMetadataId(long sourceItemPersonalMetadataId);

    List<UserItemRelation> findByTargetItemPersonalMetadataId(long targetItemPersonalMetadataId);

    List<UserItemRelation> findBySourceItemPersonalMetadataIdAndTargetItemPersonalMetadataId(
            long sourceItemPersonalMetadataId, long targetItemPersonalMetadataId);
}