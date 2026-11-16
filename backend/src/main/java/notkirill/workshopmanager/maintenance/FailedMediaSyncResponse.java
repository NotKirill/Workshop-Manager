package notkirill.workshopmanager.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.media.enums.EntityType;

import java.time.OffsetDateTime;

public record FailedMediaSyncResponse(
        @JsonProperty("entity") EntityType entity,
        @JsonProperty("entity_id") long entityId,
        @JsonProperty("title") String title,
        @JsonProperty("failures") short failures,
        @JsonProperty("last_error") String lastError,
        @JsonProperty("time_synced") OffsetDateTime timeSynced
) {
}
