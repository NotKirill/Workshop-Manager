package notkirill.workshopmanager.content.worker;

import notkirill.workshopmanager.common.Directories;
import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.content.ContentProperties;
import notkirill.workshopmanager.content.ContentRevisionService;
import notkirill.workshopmanager.content.enums.ContentJobError;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.content.fetch.ContentFetchException;
import notkirill.workshopmanager.content.fetch.ContentFetcher;
import notkirill.workshopmanager.content.fetch.FetchedContent;
import notkirill.workshopmanager.content.models.ContentJob;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.item.shared.ItemMetadataInternal;
import notkirill.workshopmanager.modspace.ModspaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class ContentJobProcessor {
    private final ContentJobService jobService;
    private final ContentRevisionService revisionService;
    private final ItemInternalService internalService;
    private final PersonalItemService personalService;
    private final ContentProperties properties;
    private final Map<ModspaceType, ContentFetcher> fetchers;

    @Autowired
    public ContentJobProcessor(ContentJobService jobService, ContentRevisionService revisionService,
                               ItemInternalService internalService, PersonalItemService personalService,
                               ContentProperties properties, List<ContentFetcher> fetchers) {
        this.jobService = jobService;
        this.revisionService = revisionService;
        this.internalService = internalService;
        this.personalService = personalService;
        this.properties = properties;
        Map<ModspaceType, ContentFetcher> map = new EnumMap<>(ModspaceType.class);
        fetchers.forEach(fetcher -> map.put(fetcher.type(), fetcher));
        this.fetchers = map;
    }

    public boolean canFetch(ModspaceType modspace) {
        ContentFetcher fetcher = fetchers.get(modspace);
        return fetcher != null && fetcher.isAvailable();
    }

    public int processDueJobs() {
        int processed = 0;
        while (processed < properties.jobs().batchSize()) {
            Optional<ContentJob> job = jobService.claimNext();
            if (job.isEmpty()) {
                break;
            }
            execute(job.get());
            processed++;
        }
        return processed;
    }

    private void execute(ContentJob job) {
        try {
            JobResult result = switch (job.getType()) {
                case BACKUP -> runBackup(job);
                case DELETE_REVISION -> {
                    revisionService.deleteRevisionNow(job.getInternalMetadataId(), job.getRevision());
                    yield JobResult.done("Deleted revision " + job.getRevision());
                }
                case DELETE_ALL -> {
                    revisionService.deleteRevisionsNow(job.getInternalMetadataId());
                    yield JobResult.done("Deleted all revisions");
                }
            };
            jobService.complete(job.getId(), result.status(), result.note());
        } catch (ContentFetchException e) {
            jobService.fail(job, e, e.getError(), e.isRetryable());
        } catch (RuntimeException e) {
            jobService.fail(job, e, ContentJobError.INTERNAL_ERROR, true);
        }
    }

    private JobResult runBackup(ContentJob job) {
        Optional<ItemMetadataInternal> internal = internalService.find(job.getInternalMetadataId());
        if (internal.isEmpty() || internal.get().getLifecycleState() != ItemLifecycleState.ACTIVE) {
            return JobResult.cancelled("Item is not active");
        }
        if (internal.get().getSourceModspace() == ModspaceType.LOCAL) {
            return JobResult.cancelled("Local items are backed up by uploading files");
        }
        if (job.getRequestedByUserId() == null && !personalService.hasAutoBackupUsers(internal.get().getId())) {
            return JobResult.cancelled("Nobody wants automatic backups of this item");
        }
        ContentFetcher fetcher = fetchers.get(internal.get().getSourceModspace());
        if (fetcher == null || !fetcher.isAvailable()) {
            throw new ContentFetchException(
                    ContentJobError.DOWNLOAD_UNAVAILABLE, "Content download for "
                    + internal.get().getSourceModspace() + " is not available", false
            );
        }

        Optional<String> sourceVersion = Optional.ofNullable(internal.get().getTimeUpdated())
                .map(time -> String.valueOf(time.toEpochSecond()));
        Optional<String> latestStored = revisionService.getLatestSourceVersion(internal.get().getId());
        if (sourceVersion.isPresent() && sourceVersion.equals(latestStored)) {
            return JobResult.done("Up to date (source version " + sourceVersion.get() + ")");
        }

        Path workDir = properties.tempDir().resolve("job-" + job.getId() + "-" + UUID.randomUUID());
        try {
            Files.createDirectories(workDir);
            FetchedContent fetched = fetcher.fetch(internal.get().getOriginalId(), workDir);
            revisionService.storeRevision(
                    internal.get().getId(), fetched.file(), fetched.fileName(),
                    sourceVersion.orElse(null), null
            );
            return JobResult.done("Stored new revision");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            Directories.deleteRecursively(workDir);
        }
    }

    private record JobResult(ContentJobStatus status, String note) {
        static JobResult done(String note) {
            return new JobResult(ContentJobStatus.SUCCEEDED, note);
        }

        static JobResult cancelled(String reason) {
            return new JobResult(ContentJobStatus.CANCELLED, reason);
        }
    }
}
