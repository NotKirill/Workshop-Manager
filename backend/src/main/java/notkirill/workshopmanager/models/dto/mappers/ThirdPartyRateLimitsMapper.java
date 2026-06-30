package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ThirdPartyRateLimits;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsRequest;
import notkirill.workshopmanager.models.dto.ThirdPartyRateLimitsResponse;

public class ThirdPartyRateLimitsMapper {
    public ThirdPartyRateLimitsResponse fromEntity(ThirdPartyRateLimits entity) {
        return new ThirdPartyRateLimitsResponse(entity.getServiceName(),
                entity.getUsedRequests(),
                entity.getWindowStartedAt(),
                entity.getLastRequestAt());


    }

    public ThirdPartyRateLimits toEntity(ThirdPartyRateLimitsRequest request) {
        return new ThirdPartyRateLimits(request.serviceName(),
                request.usedRequests(),
                request.windowStartedAt(),
                request.lastRequestAt());
    }
}
