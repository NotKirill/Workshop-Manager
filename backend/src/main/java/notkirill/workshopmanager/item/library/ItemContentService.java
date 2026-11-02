package notkirill.workshopmanager.item.library;

import notkirill.workshopmanager.content.*;
import notkirill.workshopmanager.content.models.ContentUpload;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.ForbiddenException;
import notkirill.workshopmanager.item.personal.ItemMetadataPersonal;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ItemContentService {
    private final PersonalItemService personalService;
    private final ItemInternalService internalService;
    private final ContentService contentService;
    private final ContentJobService contentJobService;
    private final ContentRevisionService contentRevisionService;
    private final ContentDownloadService contentDownloadService;
    private final ContentUploadService contentUploadService;

    @Autowired
    public ItemContentService(PersonalItemService personalService, ItemInternalService internalService,
                              ContentService contentService, ContentJobService contentJobService,
                              ContentRevisionService contentRevisionService,
                              ContentDownloadService contentDownloadService,
                              ContentUploadService contentUploadService) {
        this.personalService = personalService;
        this.internalService = internalService;
        this.contentService = contentService;
        this.contentJobService = contentJobService;
        this.contentRevisionService = contentRevisionService;
        this.contentDownloadService = contentDownloadService;
        this.contentUploadService = contentUploadService;
    }

    public int requestBackup(long userId, Collection<Long> personalIds) {
        List<ItemMetadataPersonal> personals = personalService.requireOwnedAll(userId, personalIds);
        Map<Long, ItemMetadataInternal> internals = internalService.findAll(
                personals.stream().map(ItemMetadataPersonal::getInternalMetadataId).toList());
        int requested = 0;
        for (ItemMetadataPersonal personal : personals) {
            ItemMetadataInternal internal = internals.get(personal.getInternalMetadataId());
            if (internal == null || internal.getSourceModspace() == ModspaceType.LOCAL) {
                continue;
            }
            contentJobService.requestBackup(internal.getId(), userId);
            requested++;
        }
        if (requested == 0 && personals.size() == 1) {
            throw new BadRequestException(
                    "BACKUP_NOT_SUPPORTED",
                    "Local items are backed up by uploading files"
            );
        }
        return requested;
    }

    public ContentService.Summary getSummary(long userId, long personalId) {
        long internalId = personalService.requireOwned(userId, personalId).getInternalMetadataId();
        return contentService.getSummary(internalId, userId);
    }

    public ContentUpload startUpload(long userId, long personalId, String fileName, long size) {
        long internalId = personalService.requireOwned(userId, personalId).getInternalMetadataId();
        return contentUploadService.start(userId, internalId, fileName, size);
    }

    public ContentUpload getUpload(long userId, long personalId, long uploadId) {
        long internalId = personalService.requireOwned(userId, personalId).getInternalMetadataId();
        return contentUploadService.require(userId, internalId, uploadId);
    }

    public ContentUploadService.UploadProgress appendUpload(long userId, long personalId, long uploadId, long offset,
                                                            long length, InputStream body) {
        long internalId = personalService.requireOwned(userId, personalId).getInternalMetadataId();
        return contentUploadService.append(userId, internalId, uploadId, offset, length, body);
    }

    public void cancelUpload(long userId, long personalId, long uploadId) {
        long internalId = personalService.requireOwned(userId, personalId).getInternalMetadataId();
        contentUploadService.cancel(userId, internalId, uploadId);
    }

    public void deleteAll(long userId, long personalId, boolean canManageContent) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        requireSharedContentControl(personal, canManageContent);
        contentJobService.requestDeleteAll(personal.getInternalMetadataId(), userId);
    }

    public void deleteRevision(long userId, long personalId, long revision, boolean canManageContent) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        contentService.requestDeleteRevision(
                personal.getInternalMetadataId(), revision, userId,
                controlsSharedContent(personal, canManageContent)
        );
    }

    public void setMaxRevisions(long userId, long personalId, short maxRevisions, boolean canManageContent) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        requireSharedContentControl(personal, canManageContent);
        contentRevisionService.setMaxRevisions(personal.getInternalMetadataId(), maxRevisions);
    }

    public ContentDownloadService.DownloadLink createDownloadLink(long userId, long personalId, long revision) {
        ItemMetadataPersonal personal = personalService.requireOwned(userId, personalId);
        return contentDownloadService.createDownloadLink(personal.getInternalMetadataId(), revision, userId);
    }

    private boolean controlsSharedContent(ItemMetadataPersonal personal, boolean canManageContent) {
        return canManageContent || personalService.isOnlyUser(personal.getInternalMetadataId());
    }

    private void requireSharedContentControl(ItemMetadataPersonal personal, boolean canManageContent) {
        if (!controlsSharedContent(personal, canManageContent)) {
            throw new ForbiddenException(
                    "CONTENT_SHARED",
                    "Other users rely on these backups; only content managers can change them"
            );
        }
    }
}
