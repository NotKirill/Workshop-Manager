package notkirill.workshopmanager.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.time.OffsetDateTime;

public record FailedCleanupResponse(
        @JsonProperty("internal_id") long internalId,
        @JsonProperty("modspace") ModspaceType modspace,
        @JsonProperty("original_id") String originalId,
        @JsonProperty("state") ItemLifecycleState state,
        @JsonProperty("attempts") short attempts,
        @JsonProperty("last_error") String lastError,
        @JsonProperty("orphaned_at") OffsetDateTime orphanedAt
) {
}
