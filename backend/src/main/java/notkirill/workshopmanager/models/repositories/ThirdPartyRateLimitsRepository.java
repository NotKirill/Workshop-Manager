package notkirill.workshopmanager.models.repositories;

import notkirill.workshopmanager.models.ThirdPartyRateLimits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThirdPartyRateLimitsRepository extends JpaRepository<ThirdPartyRateLimits, String> {
}