package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.ItemContentRevision;
import notkirill.workshopmanager.models.dto.ItemContentRevisionRequest;
import notkirill.workshopmanager.models.dto.ItemContentRevisionResponse;

public class ItemContentRevisionMapper {
    public ItemContentRevisionResponse fromEntity(ItemContentRevision entity) {
        return new ItemContentRevisionResponse(entity.getItemContentId(),
                entity.getRevision(),
                entity.getS3Key(),
                entity.getFileName(),
                entity.getFileSize(),
                entity.getTimeCreated());
    }

    public ItemContentRevision toEntity(ItemContentRevisionRequest request) {
        return new ItemContentRevision(request.itemContentId(),
                request.revision(),
                request.s3Key(),
                request.fileName(),
                request.fileSize(),
                request.timeCreated());
    }
}
