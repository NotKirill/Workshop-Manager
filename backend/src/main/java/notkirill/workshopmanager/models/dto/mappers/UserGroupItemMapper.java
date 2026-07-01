package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserGroupItem;
import notkirill.workshopmanager.models.dto.UserGroupItemRequest;
import notkirill.workshopmanager.models.dto.UserGroupItemResponse;

public class UserGroupItemMapper {
    public UserGroupItemResponse entityToResponse(UserGroupItem entity) {
        return new UserGroupItemResponse(entity.getUserGroupId(),
                entity.getItemPersonalMetadataId());
    }

    public UserGroupItem requestToEntity(UserGroupItemRequest request) {
        return new UserGroupItem(request.userGroupId(),
                request.itemPersonalMetadataId());
    }

    public void updateEntityFromRequest(UserGroupItemRequest request, UserGroupItem entity) {
        entity.setUserGroupId(request.userGroupId());
        entity.setItemPersonalMetadataId(request.itemPersonalMetadataId());
    }
}
