package notkirill.workshopmanager.content.repositories;

import notkirill.workshopmanager.content.models.ContentUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ContentUploadRepository extends JpaRepository<ContentUpload, Long> {
    Optional<ContentUpload> findByIdAndUserIdAndInternalMetadataId(long id, long userId, long internalMetadataId);

    List<ContentUpload> findByTimeUpdatedBefore(OffsetDateTime time);
}
