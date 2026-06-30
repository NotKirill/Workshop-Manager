package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserGroupItem;
import notkirill.workshopmanager.models.idClasses.UserGroupItemComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGroupItemRepository extends JpaRepository<UserGroupItem, UserGroupItemComplexKey> {
    Optional<UserGroupItem> findByUserGroupId(long userGroupId);

    Optional<UserGroupItem> findByItemPersonalMetadataId(long itemPersonalMetadataId);
}