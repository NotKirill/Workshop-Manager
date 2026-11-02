package notkirill.workshopmanager.user.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import notkirill.workshopmanager.user.web.IdentityResponse;

import java.time.OffsetDateTime;
import java.util.List;

public record AdminUserResponse(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("time_created") OffsetDateTime timeCreated,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("account_non_locked") boolean accountNonLocked,
        @JsonProperty("has_password") boolean hasPassword,
        @JsonProperty("permissions") List<PermissionEnum> permissions,
        @JsonProperty("identities") List<IdentityResponse> identities,
        @JsonProperty("item_count") long itemCount
) {
}
