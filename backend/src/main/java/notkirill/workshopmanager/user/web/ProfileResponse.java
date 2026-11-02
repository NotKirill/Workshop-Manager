package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record ProfileResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("time_created") OffsetDateTime timeCreated,
        @JsonProperty("has_password") boolean hasPassword,
        @JsonProperty("permissions") List<PermissionInfoResponse> permissions,
        @JsonProperty("identities") List<IdentityResponse> identities,
        @JsonProperty("item_count") long itemCount
) {
}
