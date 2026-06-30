package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserItemRelation;
import notkirill.workshopmanager.models.idClasses.UserItemRelationComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserItemRelationRepository extends JpaRepository<UserItemRelation, UserItemRelationComplexKey> {
    Optional<UserItemRelation> findBySourceItemPersonalMetadataId(long sourceItemPersonalMetadataId);

    Optional<UserItemRelation> findByTargetItemPersonalMetadataId(long targetItemPersonalMetadataId);

    Optional<UserItemRelation> findBySourceItemPersonalMetadataIdAndTargetItemPersonalMetadataId(long sourceItemPersonalMetadataId, long targetItemPersonalMetadataId);
}