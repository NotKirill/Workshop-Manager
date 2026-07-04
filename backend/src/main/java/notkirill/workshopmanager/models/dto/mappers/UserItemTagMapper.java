package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.UserItemTag;
import notkirill.workshopmanager.models.dto.UserItemTagRequest;
import notkirill.workshopmanager.models.dto.UserItemTagResponse;
import org.springframework.stereotype.Component;

@Component
public class UserItemTagMapper {
    public UserItemTagResponse entityToResponse(UserItemTag entity) {
        return new UserItemTagResponse(entity.getTagId(),
                entity.getItemPersonalMetadataId());
    }

    public UserItemTag requestToEntity(UserItemTagRequest request) {
        return new UserItemTag(request.tagId(),
                request.itemPersonalMetadataId());
    }

    public void updateEntityFromRequest(UserItemTagRequest request, UserItemTag entity) {
        entity.setTagId(request.tagId());
        entity.setItemPersonalMetadataId(request.itemPersonalMetadataId());
    }
}
