package notkirill.workshopmanager.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.enums.ContentJobType;

import java.time.OffsetDateTime;

public record ContentJobResponse(
        @JsonProperty("id") long id,
        @JsonProperty("internal_id") long internalId,
        @JsonProperty("title") String title,
        @JsonProperty("type") ContentJobType type,
        @JsonProperty("status") ContentJobStatus status,
        @JsonProperty("revision") Long revision,
        @JsonProperty("attempts") short attempts,
        @JsonProperty("next_attempt_at") OffsetDateTime nextAttemptAt,
        @JsonProperty("last_error") String lastError,
        @JsonProperty("note") String note,
        @JsonProperty("time_created") OffsetDateTime timeCreated,
        @JsonProperty("time_finished") OffsetDateTime timeFinished
) {
}
