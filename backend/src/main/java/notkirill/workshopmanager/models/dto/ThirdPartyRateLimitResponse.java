package notkirill.workshopmanager.models.dto;

import java.time.OffsetDateTime;

public record ThirdPartyRateLimitResponse(
        String serviceName,
        Integer usedRequests,
        OffsetDateTime windowStartedAt,
        OffsetDateTime lastRequestAt
) {
}
