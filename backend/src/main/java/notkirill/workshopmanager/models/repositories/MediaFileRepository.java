package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findBySourceId(String sourceId);
}