package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.Permission;
import notkirill.workshopmanager.models.dto.PermissionRequest;
import notkirill.workshopmanager.models.dto.PermissionResponse;

public class PermissionMapper {
    public PermissionResponse fromEntity(Permission entity) {
        return new PermissionResponse(entity.getId(),
                entity.getName(),
                entity.getDescription());


    }

    public Permission toEntity(PermissionRequest request) {
        return new Permission(request.name(),
                request.description());
    }
}
