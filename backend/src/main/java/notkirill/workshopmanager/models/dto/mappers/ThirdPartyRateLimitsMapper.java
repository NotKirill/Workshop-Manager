package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ThirdPartyRateLimits;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsResponse;

public class ThirdPartyRateLimitsMapper {
    public ThirdPartyRateLimitsResponse entityToResponse(ThirdPartyRateLimits entity) {
        return new ThirdPartyRateLimitsResponse(entity.getServiceName(),
                entity.getUsedRequests(),
                entity.getWindowStartedAt(),
                entity.getLastRequestAt());
    }

    public ThirdPartyRateLimits requestToEntity(ThirdPartyRateLimitsRequest request) {
        return new ThirdPartyRateLimits(request.serviceName(),
                request.usedRequests(),
                request.windowStartedAt(),
                request.lastRequestAt());
    }

    public void updateEntityFromRequest(ThirdPartyRateLimitsRequest request, ThirdPartyRateLimits entity) {
        entity.setServiceName(request.serviceName());
        entity.setUsedRequests(request.usedRequests());
        entity.setWindowStartedAt(request.windowStartedAt());
        entity.setLastRequestAt(request.lastRequestAt());
    }
}
