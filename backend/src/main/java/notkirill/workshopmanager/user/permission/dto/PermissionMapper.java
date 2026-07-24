package notkirill.workshopmanager.user.permission.dto;

import notkirill.workshopmanager.user.permission.Permission;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public PermissionResponse entityToResponseDto(Permission entity) {
        return new PermissionResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public void updateEntityFromEntity(Permission oldEntity, Permission newEntity) {
        oldEntity.setName(newEntity.getName());
        oldEntity.setDescription(newEntity.getDescription());
    }

    public PermissionEnum entityToEnum(Permission entity) {
        return PermissionEnum.getByTrueName(entity.getName());
    }
}
