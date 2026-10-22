package notkirill.workshopmanager.item.organization.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface UserItemTagRepository extends JpaRepository<UserItemTag, UserItemTag.CompositeId> {
    List<UserItemTag> findByTagId(long tagId);

    List<UserItemTag> findByItemPersonalMetadataId(long itemPersonalMetadataId);

    List<UserItemTag> findByItemPersonalMetadataIdIn(Collection<Long> itemPersonalMetadataIds);

    long countByTagId(long tagId);

    @Modifying
    @Query("DELETE FROM UserItemTag t WHERE t.itemPersonalMetadataId IN :personalIds")
    int deleteByItemPersonalMetadataIdIn(Collection<Long> personalIds);

    @Modifying
    @Query("DELETE FROM UserItemTag t WHERE t.tagId = :tagId")
    int deleteByTagId(long tagId);

    @Modifying
    @Query("DELETE FROM UserItemTag t WHERE t.tagId = :tagId AND t.itemPersonalMetadataId IN :personalIds")
    int deleteByTagIdAndItemPersonalMetadataIdIn(long tagId, Collection<Long> personalIds);
}
