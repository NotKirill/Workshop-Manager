package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.Permission;
import notkirill.workshopmanager.models.dto.PermissionRequest;
import notkirill.workshopmanager.models.dto.PermissionResponse;

public class PermissionMapper {
    public PermissionResponse entityToResponse(Permission entity) {
        return new PermissionResponse(entity.getId(),
                entity.getName(),
                entity.getDescription());
    }

    public Permission requestToEntity(PermissionRequest request) {
        return new Permission(request.name(),
                request.description());
    }

    public void updateEntityFromRequest(PermissionRequest request, Permission entity) {
        entity.setName(request.name());
        entity.setDescription(request.description());
    }
}
