package notkirill.workshopmanager.content;

import notkirill.workshopmanager.content.models.ItemContentRevision;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.security.SignedUrlService;
import notkirill.workshopmanager.storage.ObjectStorage;
import notkirill.workshopmanager.storage.StoredObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentDownloadService {
    private final ContentRevisionService revisionService;
    private final ObjectStorage storage;
    private final SignedUrlService signedUrlService;
    private final ContentProperties properties;

    @Autowired
    public ContentDownloadService(ContentRevisionService revisionService, ObjectStorage storage,
                                  SignedUrlService signedUrlService, ContentProperties properties) {
        this.revisionService = revisionService;
        this.storage = storage;
        this.signedUrlService = signedUrlService;
        this.properties = properties;
    }

    private static String contentResource(long contentId, long revision) {
        return "content:" + contentId + ":" + revision;
    }

    @Transactional(readOnly = true)
    public DownloadLink createDownloadLink(long internalId, long revisionNumber, long userId) {
        ItemContentRevision revision = revisionService.requireVisible(internalId, revisionNumber, userId);
        long contentId = revision.getItemContentId();
        String resource = contentResource(contentId, revision.getRevision());
        SignedUrlService.Signature signature = signedUrlService.sign(resource, properties.downloadLinkTtl());
        String url = "/api/public/downloads/content/" + contentId + "/" + revision.getRevision()
                + "?exp=" + signature.expiresAt() + "&sig=" + signature.value();
        return new DownloadLink(url, signature.expiresAt());
    }

    @Transactional(readOnly = true)
    public StoredObject openRevision(long contentId, long revisionNumber, long expiresAt, String signature) {
        if (!signedUrlService.verify(contentResource(contentId, revisionNumber), expiresAt, signature)) {
            throw new NotFoundException("LINK_EXPIRED", "Download link is invalid or expired");
        }
        ItemContentRevision revision = revisionService.find(contentId, revisionNumber)
                .orElseThrow(() -> new NotFoundException("REVISION_NOT_FOUND", "Backup revision not found"));
        return new StoredObject(
                storage.get(revision.getS3Key()), revision.getFileName(),
                ContentRevisionService.BACKUP_CONTENT_TYPE, revision.getFileSize()
        );
    }

    public record DownloadLink(String url, long expiresAt) {
    }
}
