package notkirill.workshopmanager.media.repositories;

import notkirill.workshopmanager.media.models.MediaEmbed;
import notkirill.workshopmanager.media.enums.MediaProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaEmbedRepository extends JpaRepository<MediaEmbed, Long> {
    Optional<MediaEmbed> findByProviderAndExternalId(MediaProvider provider, String externalId);
}