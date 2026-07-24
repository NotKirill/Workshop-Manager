package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ThirdPartyRateLimitRequest(
        @NotNull String serviceName,
        @NotNull Integer usedRequests,
        @NotNull OffsetDateTime windowStartedAt,
        @NotNull OffsetDateTime lastRequestAt
) {
}
