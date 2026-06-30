package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserSharedPermission;
import notkirill.workshopmanager.models.dto.UserSharedPermissionRequest;
import notkirill.workshopmanager.models.dto.UserSharedPermissionResponse;

public class UserSharedPermissionMapper {
    public UserSharedPermissionResponse fromEntity(UserSharedPermission entity) {
        return new UserSharedPermissionResponse(entity.getUserProviderId(),
                entity.getUserConsumerId(),
                entity.getPermissionId());


    }

    public UserSharedPermission toEntity(UserSharedPermissionRequest request) {
        return new UserSharedPermission(request.userProviderId(),
                request.userConsumerId(),
                request.permissionId());
    }
}
