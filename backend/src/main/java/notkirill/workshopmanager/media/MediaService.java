package notkirill.workshopmanager.media;

import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.enums.MediaType;
import notkirill.workshopmanager.media.models.MediaItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MediaService {
    private static final Logger log = LoggerFactory.getLogger(MediaService.class);

    private final MediaItemService itemService;
    private final MediaLinkService linkService;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public MediaService(MediaItemService itemService, MediaLinkService linkService,
                        PlatformTransactionManager transactionManager) {
        this.itemService = itemService;
        this.linkService = linkService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> sourceImageUrls(EntityType entity, Collection<Long> entityIds, String purpose) {
        Map<Long, Long> mediaItems = linkService.singleSourceMediaItems(entity, entityIds, purpose);
        Map<Long, MediaItemDetails> details = itemService.describe(mediaItems.values());
        Map<Long, String> urls = new HashMap<>();
        mediaItems.forEach((entityId, mediaItemId) -> {
            MediaItemDetails item = details.get(mediaItemId);
            if (item.type() == MediaType.FILE) {
                urls.put(entityId, item.url());
            }
        });
        return urls;
    }

    @Transactional(readOnly = true)
    public Map<Long, String> singleImageUrls(long userId, EntityType entity, Collection<Long> entityIds,
                                             String purpose) {
        Map<Long, Long> mediaItems = linkService.singleMediaItems(userId, entity, entityIds, purpose);
        Map<Long, MediaItemDetails> details = itemService.describe(mediaItems.values());
        Map<Long, String> urls = new HashMap<>();
        mediaItems.forEach((entityId, mediaItemId) -> {
            MediaItemDetails item = details.get(mediaItemId);
            if (item.type() == MediaType.FILE) {
                urls.put(entityId, item.url());
            }
        });
        return urls;
    }

    @Transactional(readOnly = true)
    public List<MediaEntry> list(long userId, EntityType entity, long entityId) {
        return toEntries(linkService.view(userId, entity, entityId));
    }

    public MediaEntry uploadImage(long userId, EntityType entity, long entityId, ImageUpload upload,
                                  String purpose) {
        MediaPurposes.requireValid(purpose);
        MediaItem item = itemService.createImage(userId, upload);
        try {
            return transactionTemplate.execute(status ->
                                                       toEntry(linkService.createOwnLink(
                                                               userId, entity, entityId,
                                                               item.getId(), purpose
                                                       )));
        } catch (RuntimeException e) {
            try {
                itemService.delete(item.getId());
            } catch (RuntimeException deleteError) {
                log.warn(
                        "Could not remove media item {} of a failed upload: {}", item.getId(),
                        deleteError.getMessage()
                );
            }
            throw e;
        }
    }

    @Transactional
    public MediaEntry addEmbed(long userId, EntityType entity, long entityId, String url, String purpose) {
        MediaPurposes.requireValid(purpose);
        MediaItem item = itemService.createEmbed(userId, url);
        return toEntry(linkService.createOwnLink(userId, entity, entityId, item.getId(), purpose));
    }

    @Transactional
    public MediaEntry addLink(long userId, EntityType entity, long entityId, long mediaItemId, String purpose) {
        return toEntry(linkService.addLink(userId, entity, entityId, mediaItemId, purpose));
    }

    private MediaEntry toEntry(VisibleLink link) {
        long mediaItemId = link.link().getMediaItemId();
        return new MediaEntry(link, itemService.describe(List.of(mediaItemId)).get(mediaItemId));
    }

    private List<MediaEntry> toEntries(List<VisibleLink> links) {
        Map<Long, MediaItemDetails> details = itemService.describe(
                links.stream().map(entry -> entry.link().getMediaItemId()).distinct().toList());
        return links.stream()
                .map(entry -> new MediaEntry(entry, details.get(entry.link().getMediaItemId())))
                .toList();
    }
}
