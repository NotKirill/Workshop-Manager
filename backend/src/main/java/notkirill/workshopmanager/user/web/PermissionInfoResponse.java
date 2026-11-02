package notkirill.workshopmanager.user.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.user.permission.PermissionEnum;

public record PermissionInfoResponse(
        @JsonProperty("permission") PermissionEnum permission,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description
) {
    public static PermissionInfoResponse of(PermissionEnum permission) {
        return new PermissionInfoResponse(permission, permission.getLabel(), permission.getDescription());
    }
}
