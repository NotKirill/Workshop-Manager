package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserItemTag;
import notkirill.workshopmanager.models.idClasses.UserItemTagComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemTagRepository extends JpaRepository<UserItemTag, UserItemTagComplexKey> {
    List<UserItemTag> findByTagId(long tagId);

    List<UserItemTag> findByItemPersonalMetadataId(long itemPersonalMetadataId);
}