package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ThirdPartyRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThirdPartyRateLimitRepository extends JpaRepository<ThirdPartyRateLimit, String> {
}