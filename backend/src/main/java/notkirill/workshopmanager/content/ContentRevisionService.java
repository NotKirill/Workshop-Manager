package notkirill.workshopmanager.content;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.Nullable;
import notkirill.workshopmanager.common.DatabaseLocks;
import notkirill.workshopmanager.common.FileNames;
import notkirill.workshopmanager.common.Hashes;
import notkirill.workshopmanager.content.models.ItemContent;
import notkirill.workshopmanager.content.models.ItemContentRevision;
import notkirill.workshopmanager.content.repositories.ItemContentRepository;
import notkirill.workshopmanager.content.repositories.ItemContentRevisionRepository;
import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.storage.ObjectStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentRevisionService {
    static final String BACKUP_CONTENT_TYPE = "application/octet-stream";

    private static final Logger log = LoggerFactory.getLogger(ContentRevisionService.class);

    private final ItemContentRepository contentRepository;
    private final ItemContentRevisionRepository revisionRepository;
    private final ContentJobService jobService;
    private final ObjectStorage storage;
    private final ContentProperties properties;
    private final DatabaseLocks databaseLocks;
    private final TransactionTemplate transactionTemplate;
    private final DistributionSummary downloadedRevisions;
    private final DistributionSummary uploadedRevisions;

    @Autowired
    public ContentRevisionService(ItemContentRepository contentRepository,
                                  ItemContentRevisionRepository revisionRepository, ContentJobService jobService,
                                  ObjectStorage storage, ContentProperties properties, DatabaseLocks databaseLocks,
                                  PlatformTransactionManager transactionManager, MeterRegistry meterRegistry) {
        this.contentRepository = contentRepository;
        this.revisionRepository = revisionRepository;
        this.jobService = jobService;
        this.storage = storage;
        this.properties = properties;
        this.databaseLocks = databaseLocks;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.downloadedRevisions = revisionSizes(meterRegistry, "download");
        this.uploadedRevisions = revisionSizes(meterRegistry, "upload");
    }

    static String sanitizeFileName(String name) {
        return FileNames.sanitize(name, "content.bin");
    }

    private static String revisionKey(long contentId, String fileName) {
        return "content/" + contentId
                + "/" + UUID.randomUUID()
                + "/" + sanitizeFileName(fileName);
    }

    private static DistributionSummary revisionSizes(MeterRegistry meterRegistry, String source) {
        return DistributionSummary.builder("workshop.content.revisions")
                .description("Stored backup revisions and their size")
                .baseUnit("bytes")
                .tag("source", source)
                .register(meterRegistry);
    }

    @Transactional(readOnly = true)
    public Optional<ItemContent> findContent(long internalId) {
        return contentRepository.findByInternalMetadataId(internalId);
    }

    @Transactional(readOnly = true)
    public List<ItemContent> findContents(Collection<Long> internalIds) {
        return contentRepository.findByInternalMetadataIdIn(internalIds);
    }

    @Transactional(readOnly = true)
    public List<ItemContentRevision> revisions(long contentId) {
        return revisionRepository.findByItemContentIdOrderByRevisionDesc(contentId);
    }

    @Transactional(readOnly = true)
    public List<ItemContentRevision> revisionsOf(Collection<Long> contentIds) {
        return revisionRepository.findByItemContentIdIn(contentIds);
    }

    @Transactional(readOnly = true)
    public Optional<ItemContentRevision> find(long contentId, long revisionNumber) {
        return revisionRepository.findByItemContentIdAndRevision(contentId, revisionNumber);
    }

    @Transactional(readOnly = true)
    public ItemContentRevision requireVisible(long internalId, long revisionNumber, long userId) {
        ItemContent content = contentRepository.findByInternalMetadataId(internalId)
                .orElseThrow(() -> new NotFoundException("CONTENT_NOT_FOUND", "Item has no backups"));
        return revisionRepository.findByItemContentIdAndRevision(content.getId(), revisionNumber)
                .filter(revision -> revision.isVisibleTo(userId))
                .orElseThrow(() -> new NotFoundException("REVISION_NOT_FOUND", "Backup revision not found"));
    }

    @Transactional(readOnly = true)
    public List<ItemContentRevision> uploadsOf(long internalId, long userId) {
        return contentRepository.findByInternalMetadataId(internalId)
                .map(content -> revisionRepository.findByItemContentIdAndUploadedByUserId(content.getId(), userId))
                .orElse(List.of());
    }

    public Optional<String> getLatestSourceVersion(long internalId) {
        return contentRepository.findByInternalMetadataId(internalId)
                .flatMap(c -> revisionRepository.findFirstByItemContentIdAndUploadedByUserIdIsNullOrderByRevisionDesc(
                        c.getId()))
                .map(ItemContentRevision::getSourceVersion);
    }

    public ItemContentRevision storeRevision(long internalId, Path file, String fileName, String sourceVersion,
                                             @Nullable Long uploadedByUserId) {
        String sha256;
        long size;
        try {
            size = Files.size(file);
            sha256 = Hashes.sha256Hex(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read content file", e);
        }
        PreparedContent prepared = transactionTemplate.execute(status -> {
            databaseLocks.lock("content:" + internalId);
            return contentRepository.findByInternalMetadataId(internalId)
                    .map(existing -> new PreparedContent(existing, false))
                    .orElseGet(() -> new PreparedContent(createContent(internalId), true));
        });
        String key = revisionKey(prepared.content().getId(), fileName);

        try {
            storage.put(key, file, BACKUP_CONTENT_TYPE);
            ItemContentRevision stored = transactionTemplate.execute(status -> {
                databaseLocks.lock("content:" + internalId);
                ItemContent content = getOrCreateContent(internalId);
                long nextRevision = revisionRepository.findMaxRevision(content.getId()) + 1;
                ItemContentRevision revision = revisionRepository.save(new ItemContentRevision(
                        content.getId(),
                        nextRevision, key, sanitizeFileName(fileName), size, sha256, sourceVersion, uploadedByUserId
                ));
                enforceMaxRevisions(content);
                return revision;
            });
            if (uploadedByUserId == null) downloadedRevisions.record(size);
            else uploadedRevisions.record(size);
            return stored;
        } catch (RuntimeException e) {
            try {
                storage.delete(key);
            } catch (RuntimeException deleteError) {
                log.warn("Could not remove orphan object {}: {}", key, deleteError.getMessage());
            }
            if (prepared.created()) {
                discardEmptyContent(internalId, prepared.content().getId());
            }
            throw e;
        }
    }

    @Transactional
    public void setMaxRevisions(long internalId, short maxRevisions) {
        if (maxRevisions < 1 || maxRevisions > 50) {
            throw new BadRequestException("INVALID_MAX_REVISIONS", "Max revisions must be between 1 and 50");
        }
        databaseLocks.lock("content:" + internalId);
        ItemContent content = getOrCreateContent(internalId);
        content.setMaxRevisions(maxRevisions);
        contentRepository.save(content);
        enforceMaxRevisions(content);
    }

    public void deleteRevisionNow(long internalId, long revisionNumber) {
        Optional<ItemContent> content = contentRepository.findByInternalMetadataId(internalId);
        if (content.isEmpty()) {
            return;
        }
        Optional<ItemContentRevision> revision =
                revisionRepository.findByItemContentIdAndRevision(content.get().getId(), revisionNumber);
        if (revision.isEmpty()) {
            return;
        }
        storage.delete(revision.get().getS3Key());
        transactionTemplate.executeWithoutResult(status ->
                                                         revisionRepository.findByItemContentIdAndRevision(
                                                                         content.get().getId(), revisionNumber)
                                                                 .ifPresent(revisionRepository::delete));
    }

    public void deleteRevisionsNow(long internalId) {
        Optional<ItemContent> content = contentRepository.findByInternalMetadataId(internalId);
        if (content.isEmpty()) {
            return;
        }
        for (ItemContentRevision revision : revisionRepository.findByItemContentIdOrderByRevisionDesc(
                content.get().getId())) {
            storage.delete(revision.getS3Key());
            transactionTemplate.executeWithoutResult(status -> revisionRepository.delete(revision));
        }
        jobService.cancelPending(internalId, "Content deleted");
    }

    public void deleteContentNow(long internalId) {
        deleteRevisionsNow(internalId);
        transactionTemplate.executeWithoutResult(status ->
                                                         contentRepository.findByInternalMetadataId(
                                                                 internalId).ifPresent(contentRepository::delete));
    }

    private void discardEmptyContent(long internalId, long contentId) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                databaseLocks.lock("content:" + internalId);
                if (revisionRepository.findMaxRevision(contentId) == 0) {
                    contentRepository.deleteById(contentId);
                }
            });
        } catch (RuntimeException e) {
            log.warn("Could not remove empty content record {}: {}", contentId, e.getMessage());
        }
    }

    private void enforceMaxRevisions(ItemContent content) {
        Map<Optional<Long>, List<ItemContentRevision>> byUploader = revisionRepository
                .findByItemContentIdOrderByRevisionDesc(content.getId()).stream()
                .collect(Collectors.groupingBy(revision -> Optional.ofNullable(revision.getUploadedByUserId())));
        for (List<ItemContentRevision> revisions : byUploader.values()) {
            if (revisions.size() <= content.getMaxRevisions()) {
                continue;
            }
            for (ItemContentRevision old : revisions.subList(content.getMaxRevisions(), revisions.size())) {
                jobService.requestDeleteRevision(content.getInternalMetadataId(), old.getRevision(), null);
            }
        }
    }

    private ItemContent getOrCreateContent(long internalId) {
        return contentRepository.findByInternalMetadataId(internalId)
                .orElseGet(() -> createContent(internalId));
    }

    private ItemContent createContent(long internalId) {
        return contentRepository.save(new ItemContent(internalId, "latest", null, properties.defaultMaxRevisions()));
    }

    private record PreparedContent(ItemContent content, boolean created) {
    }
}
