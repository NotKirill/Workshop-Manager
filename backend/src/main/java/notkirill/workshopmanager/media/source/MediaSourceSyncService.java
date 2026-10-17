package notkirill.workshopmanager.media.source;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.common.Texts;
import notkirill.workshopmanager.exception.NotFoundException;
import notkirill.workshopmanager.media.ImageFormat;
import notkirill.workshopmanager.media.MediaItemService;
import notkirill.workshopmanager.media.MediaLinkService;
import notkirill.workshopmanager.media.MediaProperties;
import notkirill.workshopmanager.media.enums.EntityType;
import notkirill.workshopmanager.media.models.MediaLink;
import notkirill.workshopmanager.media.models.MediaSourceSync;
import notkirill.workshopmanager.media.repositories.MediaSourceSyncRepository;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MediaSourceSyncService {
    private static final Logger log = LoggerFactory.getLogger(MediaSourceSyncService.class);
    private static final Duration CLAIM_LEASE = Duration.ofMinutes(15);

    private final MediaSourceSyncRepository repository;
    private final MediaItemService itemService;
    private final MediaLinkService linkService;
    private final SourceMediaDownloader downloader;
    private final Map<ModspaceType, SourceMediaProvider> providers;
    private final MediaProperties properties;
    private final Clock clock;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public MediaSourceSyncService(MediaSourceSyncRepository repository, MediaItemService itemService,
                                  MediaLinkService linkService, SourceMediaDownloader downloader,
                                  List<SourceMediaProvider> providers, MediaProperties properties, Clock clock,
                                  PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.itemService = itemService;
        this.linkService = linkService;
        this.downloader = downloader;
        this.providers = providers.stream()
                .collect(Collectors.toMap(SourceMediaProvider::modspace, Function.identity()));
        this.properties = properties;
        this.clock = clock;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Transactional
    public void onSourceSynced(EntityType entity, long entityId, ModspaceType modspace, String originalId,
                               boolean mediaChanged) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        if (mediaChanged) {
            repository.requestNow(entity.name(), entityId, modspace.name(), originalId, now);
        } else {
            repository.createIfMissing(entity.name(), entityId, modspace.name(), originalId, now);
        }
    }

    public int syncDue() {
        List<MediaSourceSync> claimed = transactionTemplate.execute(status -> {
            OffsetDateTime now = OffsetDateTime.now(clock);
            List<MediaSourceSync> due = repository.claimDue(
                    now, properties.syncMaxFailures(),
                    properties.syncBatchSize()
            );
            due.forEach(state -> state.setDueAt(now.plus(CLAIM_LEASE)));
            return due;
        });
        claimed.forEach(this::sync);
        return claimed.size();
    }

    @Transactional(readOnly = true)
    public long countGivenUp() {
        return repository.countByFailuresGreaterThanEqual(properties.syncMaxFailures());
    }

    @Transactional(readOnly = true)
    public List<MediaSourceSync> givenUp() {
        return repository.findByFailuresGreaterThanEqualOrderByDueAtDesc(properties.syncMaxFailures());
    }

    @Transactional
    public void retry(EntityType entity, long entityId) {
        MediaSourceSync state = repository.findById(new MediaSourceSync.CompositeId(entity, entityId))
                .orElseThrow(() -> new NotFoundException("MEDIA_SYNC_NOT_FOUND", "Media sync not found"));
        state.setFailures((short) 0);
        state.setDueAt(OffsetDateTime.now(clock));
    }

    public void deleteAll(EntityType entity, long entityId) {
        linkService.sourceLinks(entity, entityId).forEach(link -> linkService.removeSourceLink(link.getId()));
        transactionTemplate.executeWithoutResult(status ->
                                                         repository.deleteById(
                                                                 new MediaSourceSync.CompositeId(entity, entityId)));
    }

    private void sync(MediaSourceSync state) {
        try {
            Source source = readSource(state);
            reconcile(state.getEntity(), state.getEntityId(), source.entries(), source.provider());
            markSynced(state);
        } catch (RuntimeException e) {
            markFailed(state, e);
        }
    }

    private Source readSource(MediaSourceSync state) {
        if (state.getSourceModspace() == null || state.getSourceOriginalId() == null) {
            return Source.NONE;
        }
        SourceMediaProvider provider = providers.get(state.getSourceModspace());
        return provider == null
                ? Source.NONE
                : new Source(provider, provider.describe(state.getEntity(), state.getSourceOriginalId()));
    }

    private void reconcile(EntityType entity, long entityId, List<SourceMedia> entries,
                           @Nullable SourceMediaProvider provider) {
        Map<String, MediaLink> links = new HashMap<>();
        for (MediaLink link : linkService.sourceLinks(entity, entityId)) {
            links.put(link.getSourceKey(), link);
        }
        Map<Long, String> versions = itemService.sourceVersions(
                links.values().stream().map(MediaLink::getMediaItemId).toList());
        List<String> failed = new ArrayList<>();
        for (SourceMedia entry : entries) {
            MediaLink link = links.remove(entry.key());
            if (link != null && entry.content().version().equals(versions.get(link.getMediaItemId()))) {
                linkService.updateSourceLink(link.getId(), entry.purpose(), entry.sortOrder());
                continue;
            }
            try {
                storeAndLink(entity, entityId, entry, link, provider);
            } catch (SourceMediaException e) {
                if (e.isPermanent()) {
                    log.info("Skipped source media {} of {} {}: {}", entry.key(), entity, entityId, e.getMessage());
                } else {
                    failed.add(entry.key() + ": " + e.getMessage());
                }
            }
        }
        links.values().forEach(link -> linkService.removeSourceLink(link.getId()));
        if (!failed.isEmpty()) {
            throw new SourceMediaException(
                    failed.size() + " of " + entries.size() + " entries failed: "
                            + String.join("; ", failed), false
            );
        }
    }

    private void storeAndLink(EntityType entity, long entityId, SourceMedia entry, @Nullable MediaLink link,
                              SourceMediaProvider provider) {
        long mediaItemId = store(entry.content(), provider);
        try {
            if (link == null) {
                linkService.createSourceLink(
                        entity, entityId, mediaItemId, entry.key(), entry.purpose(),
                        entry.sortOrder()
                );
            } else {
                linkService.replaceSourceMedia(link.getId(), mediaItemId, entry.purpose(), entry.sortOrder());
            }
        } catch (RuntimeException e) {
            itemService.delete(mediaItemId);
            throw e;
        }
    }

    @SuppressWarnings("java:S6878")
    private long store(SourceMedia.Content content, SourceMediaProvider provider) {
        return switch (content) {
            case SourceMedia.RemoteFile file -> {
                SourceMediaDownloader.DownloadedFile downloaded = provider.download(file.url(), downloader);
                ImageFormat format = ImageFormat.detect(downloaded.data())
                        .orElseThrow(() -> new SourceMediaException("Not a supported image: " + file.url(), true));
                yield itemService.createSourceImage(downloaded.data(), downloaded.fileName(), format, file.version())
                        .getId();
            }
            case SourceMedia.Embed embed -> itemService.createEmbed(null, embed.provider(), embed.externalId())
                    .getId();
        };
    }

    private void markSynced(MediaSourceSync state) {
        MediaSourceSync.CompositeId id = new MediaSourceSync.CompositeId(state.getEntity(), state.getEntityId());
        transactionTemplate.executeWithoutResult(status -> repository.findById(id)
                .ifPresent(found -> {
                    found.setDueAt(null);
                    found.setFailures((short) 0);
                    found.setLastError(null);
                    found.setTimeSynced(OffsetDateTime.now(clock));
                }));
    }

    private void markFailed(MediaSourceSync state, RuntimeException error) {
        short failures = (short) (state.getFailures() + 1);
        log.warn(
                "Source media of {} {} failed ({} of {} attempts): {}", state.getEntity(), state.getEntityId(),
                failures, properties.syncMaxFailures(), error.getMessage()
        );
        Duration pause = properties.syncRetryBaseDelay().multipliedBy(1L << (failures - 1));
        MediaSourceSync.CompositeId id = new MediaSourceSync.CompositeId(state.getEntity(), state.getEntityId());
        transactionTemplate.executeWithoutResult(status -> repository.findById(id)
                .ifPresent(found -> {
                    found.setFailures(failures);
                    found.setLastError(Texts.errorMessage(error));
                    found.setDueAt(OffsetDateTime.now(clock).plus(pause));
                }));
    }

    private record Source(@Nullable SourceMediaProvider provider, List<SourceMedia> entries) {
        static final Source NONE = new Source(null, List.of());
    }
}
