package notkirill.workshopmanager.security.auth.me;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record MeResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("time_created") OffsetDateTime timeCreated
) {
}
