package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.MediaEmbed;
import notkirill.workshopmanager.models.enums.MediaProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaEmbedRepository extends JpaRepository<MediaEmbed, Long> {
    Optional<MediaEmbed> findByProviderAndExternalId(MediaProvider provider, String externalId);
}