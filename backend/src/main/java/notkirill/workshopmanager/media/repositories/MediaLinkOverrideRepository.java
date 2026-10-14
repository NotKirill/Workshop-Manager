package notkirill.workshopmanager.media.repositories;

import notkirill.workshopmanager.media.models.MediaLinkOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MediaLinkOverrideRepository extends JpaRepository<MediaLinkOverride, MediaLinkOverride.CompositeId> {
    List<MediaLinkOverride> findByLinkId(long linkId);

    List<MediaLinkOverride> findByUserId(long userId);

    Optional<MediaLinkOverride> findByLinkIdAndUserId(long linkId, long userId);

    List<MediaLinkOverride> findByUserIdAndLinkIdIn(long userId, Collection<Long> linkIds);

    void deleteByUserIdAndLinkIdIn(long userId, Collection<Long> linkIds);

    void deleteByLinkId(long linkId);
}