package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserItemTag;
import notkirill.workshopmanager.models.idClasses.UserItemTagComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserItemTagRepository extends JpaRepository<UserItemTag, UserItemTagComplexKey> {
    Optional<UserItemTag> findByTagId(long tagId);

    Optional<UserItemTag> findByItemPersonalMetadataId(long itemPersonalMetadataId);
}