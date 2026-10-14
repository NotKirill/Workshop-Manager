package notkirill.workshopmanager.media.repositories;

import notkirill.workshopmanager.media.models.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    Optional<MediaFile> findBySourceVersion(String sourceVersion);
}