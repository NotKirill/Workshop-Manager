package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserGroupItem;
import notkirill.workshopmanager.models.dto.UserGroupItemRequest;
import notkirill.workshopmanager.models.dto.UserGroupItemResponse;

public class UserGroupItemMapper {
    public UserGroupItemResponse fromEntity(UserGroupItem entity) {
        return new UserGroupItemResponse(entity.getUserGroupId(),
                entity.getItemPersonalMetadataId());


    }

    public UserGroupItem toEntity(UserGroupItemRequest request) {
        return new UserGroupItem(request.userGroupId(),
                request.itemPersonalMetadataId());
    }
}
