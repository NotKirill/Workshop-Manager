package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemContentRevision;
import notkirill.workshopmanager.models.dto.ItemContentRevisionRequest;
import notkirill.workshopmanager.models.dto.ItemContentRevisionResponse;
import org.springframework.stereotype.Component;

@Component
public class ItemContentRevisionMapper {
    public ItemContentRevisionResponse entityToResponse(ItemContentRevision entity) {
        return new ItemContentRevisionResponse(entity.getItemContentId(),
                entity.getRevision(),
                entity.getS3Key(),
                entity.getFileName(),
                entity.getFileSize(),
                entity.getTimeCreated());
    }

    public ItemContentRevision requestToEntity(ItemContentRevisionRequest request) {
        return new ItemContentRevision(request.itemContentId(),
                request.revision(),
                request.s3Key(),
                request.fileName(),
                request.fileSize(),
                request.timeCreated());
    }

    public void updateEntityFromRequest(ItemContentRevisionRequest request, ItemContentRevision entity) {
        entity.setItemContentId(request.itemContentId());
        entity.setRevision(request.revision());
        entity.setS3Key(request.s3Key());
        entity.setFileName(request.fileName());
        entity.setFileSize(request.fileSize());
        entity.setTimeCreated(request.timeCreated());
    }
}
