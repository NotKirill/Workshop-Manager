package notkirill.workshopmanager.user.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.permission.PermissionEnum;

import java.util.Map;
import java.util.Set;

public record CreateUserRequest(
        @JsonProperty("name") @Size(max = 100) String name,
        @JsonProperty("password") @Size(max = 128) String password,
        @JsonProperty("identities") Map<AuthSource, @NotBlank @Size(max = 254) String> identities,
        @JsonProperty("permissions") Set<PermissionEnum> permissions
) {
    public CreateUserRequest {
        identities = identities == null ? Map.of() : identities;
        permissions = permissions == null ? PermissionEnum.DEFAULT_USER : permissions;
    }
}
