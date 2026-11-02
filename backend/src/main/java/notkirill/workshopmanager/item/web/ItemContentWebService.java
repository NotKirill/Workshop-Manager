package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.content.ContentDownloadService;
import notkirill.workshopmanager.content.ContentUploadService;
import notkirill.workshopmanager.content.models.ContentUpload;
import notkirill.workshopmanager.content.models.ItemContentRevision;
import notkirill.workshopmanager.content.web.*;
import notkirill.workshopmanager.item.library.ItemContentService;
import notkirill.workshopmanager.item.web.dto.BulkResultResponse;
import notkirill.workshopmanager.security.PermissionAuthorities;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class ItemContentWebService {
    private final ItemContentService itemContentService;
    private final ContentResponseMapper contentResponseMapper;
    private final ContentUploadService contentUploadService;

    @Autowired
    public ItemContentWebService(ItemContentService itemContentService, ContentResponseMapper contentResponseMapper,
                                 ContentUploadService contentUploadService) {
        this.itemContentService = itemContentService;
        this.contentResponseMapper = contentResponseMapper;
        this.contentUploadService = contentUploadService;
    }

    private static boolean canManageContent(UserPrincipal principal) {
        return PermissionAuthorities.has(principal, PermissionEnum.CONTENT_MANAGE);
    }

    public BulkResultResponse requestBackup(List<Long> ids, UserPrincipal principal) {
        return new BulkResultResponse(itemContentService.requestBackup(principal.getUserId(), ids));
    }

    public ContentSummaryResponse getContent(long id, UserPrincipal principal) {
        return contentResponseMapper.toSummary(itemContentService.getSummary(principal.getUserId(), id));
    }

    public ContentUploadResponse startUpload(long id, ContentUploadRequest request, UserPrincipal principal) {
        ContentUpload upload = itemContentService.startUpload(
                principal.getUserId(), id, request.fileName(),
                request.size()
        );
        return toUploadResponse(upload, upload.getReceived(), null);
    }

    public ContentUploadResponse getUpload(long id, long uploadId, UserPrincipal principal) {
        ContentUpload upload = itemContentService.getUpload(principal.getUserId(), id, uploadId);
        return toUploadResponse(upload, upload.getReceived(), null);
    }

    public ContentUploadResponse appendUpload(long id, long uploadId, long offset, long length, InputStream body,
                                              UserPrincipal principal) {
        ContentUploadService.UploadProgress progress = itemContentService.appendUpload(
                principal.getUserId(), id,
                uploadId, offset, length, body
        );
        return toUploadResponse(progress.upload(), progress.received(), progress.revision());
    }

    public void cancelUpload(long id, long uploadId, UserPrincipal principal) {
        itemContentService.cancelUpload(principal.getUserId(), id, uploadId);
    }

    public DownloadLinkResponse createDownloadLink(long id, long revision, UserPrincipal principal) {
        ContentDownloadService.DownloadLink link = itemContentService.createDownloadLink(
                principal.getUserId(), id,
                revision
        );
        return new DownloadLinkResponse(link.url(), link.expiresAt());
    }

    public void deleteRevision(long id, long revision, UserPrincipal principal) {
        itemContentService.deleteRevision(principal.getUserId(), id, revision, canManageContent(principal));
    }

    public void deleteAll(long id, UserPrincipal principal) {
        itemContentService.deleteAll(principal.getUserId(), id, canManageContent(principal));
    }

    public void setMaxRevisions(long id, short maxRevisions, UserPrincipal principal) {
        itemContentService.setMaxRevisions(principal.getUserId(), id, maxRevisions, canManageContent(principal));
    }

    private ContentUploadResponse toUploadResponse(ContentUpload upload, long received,
                                                   ItemContentRevision revision) {
        return new ContentUploadResponse(
                upload.getId(), upload.getFileName(), upload.getSize(), received,
                contentUploadService.chunkSize(), revision == null ? null : contentResponseMapper.toResponse(revision)
        );
    }
}
