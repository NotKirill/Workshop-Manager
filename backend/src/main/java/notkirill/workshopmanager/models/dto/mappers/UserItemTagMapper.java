package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserItemTag;
import notkirill.workshopmanager.models.dto.UserItemTagRequest;
import notkirill.workshopmanager.models.dto.UserItemTagResponse;

public class UserItemTagMapper {
    public UserItemTagResponse fromEntity(UserItemTag entity) {
        return new UserItemTagResponse(entity.getTagId(),
                entity.getItemPersonalMetadataId());

    }

    public UserItemTag toEntity(UserItemTagRequest request) {
        return new UserItemTag(request.tagId(),
                request.itemPersonalMetadataId());
    }
}
