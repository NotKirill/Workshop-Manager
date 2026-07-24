package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ThirdPartyRateLimit;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitResponse;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartyRateLimitMapper {
    public ThirdPartyRateLimitResponse entityToResponse(ThirdPartyRateLimit entity) {
        return new ThirdPartyRateLimitResponse(
                entity.getServiceName(),
                entity.getUsedRequests(),
                entity.getWindowStartedAt(),
                entity.getLastRequestAt()
        );
    }

    public ThirdPartyRateLimit requestToEntity(ThirdPartyRateLimitRequest request) {
        return new ThirdPartyRateLimit(
                request.serviceName(),
                request.usedRequests(),
                request.windowStartedAt(),
                request.lastRequestAt()
        );
    }

    public void updateEntityFromRequest(ThirdPartyRateLimitRequest request, ThirdPartyRateLimit entity) {
        entity.setServiceName(request.serviceName());
        entity.setUsedRequests(request.usedRequests());
        entity.setWindowStartedAt(request.windowStartedAt());
        entity.setLastRequestAt(request.lastRequestAt());
    }
}
