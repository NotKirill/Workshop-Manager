package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaLinkOverride;
import notkirill.workshopmanager.models.idClasses.MediaLinkOverrideComplexKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaLinkOverrideRepository extends JpaRepository<MediaLinkOverride, MediaLinkOverrideComplexKey> {
    List<MediaLinkOverride> findByLinkId(long linkId);

    List<MediaLinkOverride> findByUserId(long userId);

    Optional<MediaLinkOverride> findByLinkIdAndUserId(long linkId, long userId);
}