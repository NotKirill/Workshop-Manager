package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.UserGroupItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupItemRepository extends JpaRepository<UserGroupItem, UserGroupItem.CompositeId> {
    List<UserGroupItem> findByUserGroupId(long userGroupId);

    List<UserGroupItem> findByItemPersonalMetadataId(long itemPersonalMetadataId);
}