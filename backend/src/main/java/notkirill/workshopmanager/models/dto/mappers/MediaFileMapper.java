package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaFile;
import notkirill.workshopmanager.models.dto.MediaFileRequest;
import notkirill.workshopmanager.models.dto.MediaFileResponse;

public class MediaFileMapper {
    public MediaFileResponse entityToResponse(MediaFile entity) {
        return new MediaFileResponse(entity.getMediaItemId(),
                entity.getSourceId(),
                entity.getFileSize(),
                entity.getFileName(),
                entity.getS3Key(),
                entity.getMetadata());
    }

    public MediaFile requestToEntity(MediaFileRequest request) {
        return new MediaFile(request.mediaItemId(),
                request.sourceId(),
                request.fileSize(),
                request.fileName(),
                request.s3Key(),
                request.metadata());
    }

    public void updateEntityFromRequest(MediaFileRequest request, MediaFile entity) {
        entity.setMediaItemId(request.mediaItemId());
        entity.setSourceId(request.sourceId());
        entity.setFileSize(request.fileSize());
        entity.setFileName(request.fileName());
        entity.setS3Key(request.s3Key());
        entity.setMetadata(request.metadata());
    }
}
