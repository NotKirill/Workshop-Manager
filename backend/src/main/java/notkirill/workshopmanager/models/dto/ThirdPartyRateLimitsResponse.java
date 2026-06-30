package notkirill.workshopmanager.models.dto;

import java.time.OffsetDateTime;

public record ThirdPartyRateLimitsResponse(
        String serviceName,
        Integer usedRequests,
        OffsetDateTime windowStartedAt,
        OffsetDateTime lastRequestAt
) {
}
