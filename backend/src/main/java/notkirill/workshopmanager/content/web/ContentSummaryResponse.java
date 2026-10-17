package notkirill.workshopmanager.content.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.content.enums.ContentJobError;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.enums.ContentJobType;

import java.time.OffsetDateTime;
import java.util.List;

public record ContentSummaryResponse(
        @JsonProperty("max_revisions") short maxRevisions,
        @JsonProperty("revisions") List<ContentRevisionResponse> revisions,
        @JsonProperty("active_job") Job activeJob,
        @JsonProperty("last_failed_job") Job lastFailedJob
) {
    public record Job(
            @JsonProperty("id") long id,
            @JsonProperty("type") ContentJobType type,
            @JsonProperty("status") ContentJobStatus status,
            @JsonProperty("attempts") short attempts,
            @JsonProperty("next_attempt_at") OffsetDateTime nextAttemptAt,
            @JsonProperty("error_code") ContentJobError errorCode
    ) {
    }
}
