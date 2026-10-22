package notkirill.workshopmanager.item.organization.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserGroupItemRepository extends JpaRepository<UserGroupItem, UserGroupItem.CompositeId> {
    List<UserGroupItem> findByUserGroupId(long userGroupId);

    List<UserGroupItem> findByItemPersonalMetadataId(long itemPersonalMetadataId);

    List<UserGroupItem> findByItemPersonalMetadataIdIn(Collection<Long> itemPersonalMetadataIds);

    long countByUserGroupId(long userGroupId);

    @Modifying
    @Query("DELETE FROM UserGroupItem g WHERE g.itemPersonalMetadataId IN :personalIds")
    int deleteByItemPersonalMetadataIdIn(Collection<Long> personalIds);

    @Modifying
    @Query("DELETE FROM UserGroupItem g WHERE g.userGroupId = :groupId")
    int deleteByUserGroupId(long groupId);

    @Modifying
    @Query("DELETE FROM UserGroupItem g WHERE g.userGroupId = :groupId AND g.itemPersonalMetadataId IN :personalIds")
    int deleteByUserGroupIdAndItemPersonalMetadataIdIn(long groupId, Collection<Long> personalIds);
}
