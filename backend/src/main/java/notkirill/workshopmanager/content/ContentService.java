package notkirill.workshopmanager.content;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.content.enums.ContentJobType;
import notkirill.workshopmanager.content.models.ContentJob;
import notkirill.workshopmanager.content.models.ItemContent;
import notkirill.workshopmanager.content.models.ItemContentRevision;
import notkirill.workshopmanager.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentService {
    private final ContentRevisionService revisionService;
    private final ContentJobService jobService;
    private final ContentProperties properties;

    @Autowired
    public ContentService(ContentRevisionService revisionService, ContentJobService jobService,
                          ContentProperties properties) {
        this.revisionService = revisionService;
        this.jobService = jobService;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public Summary getSummary(long internalId, long userId) {
        Optional<ItemContent> content = revisionService.findContent(internalId);
        List<ItemContentRevision> revisions = content
                .map(c -> revisionService.revisions(c.getId()).stream()
                        .filter(revision -> revision.isVisibleTo(userId))
                        .toList())
                .orElse(List.of());
        return new Summary(
                content.map(ItemContent::getMaxRevisions).orElse(properties.defaultMaxRevisions()),
                revisions, jobService.activeJob(internalId).orElse(null),
                jobService.lastFailedJob(internalId).orElse(null)
        );
    }

    @Transactional(readOnly = true)
    public Map<Long, Brief> getBriefs(long userId, Collection<Long> internalIds) {
        if (internalIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Long> contentToInternal = revisionService.findContents(internalIds).stream()
                .collect(Collectors.toMap(ItemContent::getId, ItemContent::getInternalMetadataId));
        Map<Long, List<ItemContentRevision>> revisionsByInternal = revisionService
                .revisionsOf(contentToInternal.keySet()).stream()
                .filter(revision -> revision.isVisibleTo(userId))
                .collect(Collectors.groupingBy(r -> contentToInternal.get(r.getItemContentId())));
        Map<Long, ContentJob> activeJobs = jobService.activeJobs(internalIds);
        Map<Long, Brief> result = new HashMap<>();
        for (Long internalId : internalIds) {
            List<ItemContentRevision> revisions = revisionsByInternal.getOrDefault(internalId, List.of());
            OffsetDateTime latest = revisions.stream().map(ItemContentRevision::getTimeCreated)
                    .filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(null);
            ContentJob job = activeJobs.get(internalId);
            result.put(internalId, new Brief(revisions.size(), latest, job != null ? job.getType() : null));
        }
        return result;
    }

    @Transactional
    public void requestDeleteRevision(long internalId, long revision, long requestedByUserId,
                                      boolean mayDeleteShared) {
        ItemContentRevision found = revisionService.requireVisible(internalId, revision, requestedByUserId);
        if (found.getUploadedByUserId() == null && !mayDeleteShared) {
            throw new ForbiddenException(
                    "CONTENT_SHARED",
                    "Other users rely on these backups; only content managers can change them"
            );
        }
        jobService.requestDeleteRevision(internalId, revision, requestedByUserId);
    }

    @Transactional
    public void requestDeleteUploads(long internalId, long userId) {
        for (ItemContentRevision upload : revisionService.uploadsOf(internalId, userId)) {
            jobService.requestDeleteRevision(internalId, upload.getRevision(), userId);
        }
    }

    public record Brief(int revisionCount, OffsetDateTime latestBackupAt, ContentJobType activeJob) {
    }

    public record Summary(short maxRevisions, List<ItemContentRevision> revisions, @Nullable ContentJob activeJob,
                          @Nullable ContentJob lastFailedJob) {
    }
}
