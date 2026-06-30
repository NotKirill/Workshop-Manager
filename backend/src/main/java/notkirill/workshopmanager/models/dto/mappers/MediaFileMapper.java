package notkirill.workshopmanager.models.dto.mappers;

import notkirill.workshopmanager.models.MediaFile;
import notkirill.workshopmanager.models.dto.MediaFileRequest;
import notkirill.workshopmanager.models.dto.MediaFileResponse;

public class MediaFileMapper {
    public MediaFileResponse fromEntity(MediaFile entity) {
        return new MediaFileResponse(entity.getMediaItemId(),
                entity.getSourceId(),
                entity.getFileSize(),
                entity.getFileName(),
                entity.getS3Key(),
                entity.getMetadata());

    }

    public MediaFile toEntity(MediaFileRequest request) {
        return new MediaFile(request.mediaItemId(),
                request.sourceId(),
                request.fileSize(),
                request.fileName(),
                request.s3Key(),
                request.metadata());
    }
}
