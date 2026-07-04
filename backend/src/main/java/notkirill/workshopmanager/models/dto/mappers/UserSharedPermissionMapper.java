package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserSharedPermission;
import notkirill.workshopmanager.models.dto.UserSharedPermissionRequest;
import notkirill.workshopmanager.models.dto.UserSharedPermissionResponse;
import org.springframework.stereotype.Component;

@Component
public class UserSharedPermissionMapper {
    public UserSharedPermissionResponse entityToResponse(UserSharedPermission entity) {
        return new UserSharedPermissionResponse(entity.getUserProviderId(),
                entity.getUserConsumerId(),
                entity.getPermissionId());
    }

    public UserSharedPermission requestToEntity(UserSharedPermissionRequest request) {
        return new UserSharedPermission(request.userProviderId(),
                request.userConsumerId(),
                request.permissionId());
    }

    public void updateEntityFromRequest(UserSharedPermissionRequest request, UserSharedPermission entity) {
        entity.setUserProviderId(request.userProviderId());
        entity.setUserConsumerId(request.userConsumerId());
        entity.setPermissionId(request.permissionId());
    }
}
