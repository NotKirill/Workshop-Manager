package notkirill.workshopmanager.models.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record ThirdPartyRateLimitsRequest(
        @NotNull String serviceName,
        @NotNull Integer usedRequests,
        @NotNull OffsetDateTime windowStartedAt,
        @NotNull OffsetDateTime lastRequestAt
) {
}
