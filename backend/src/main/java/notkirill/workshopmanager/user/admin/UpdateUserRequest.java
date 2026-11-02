package notkirill.workshopmanager.user.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import notkirill.workshopmanager.user.permission.PermissionEnum;

import java.util.List;

public record UpdateUserRequest(
        @JsonProperty("name") @Size(max = 100) String name,
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("account_non_locked") Boolean accountNonLocked,
        @JsonProperty("permissions") List<PermissionEnum> permissions
) {
}
