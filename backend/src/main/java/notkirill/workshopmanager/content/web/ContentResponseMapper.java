package notkirill.workshopmanager.content.web;

import jakarta.annotation.Nullable;
import notkirill.workshopmanager.content.ContentService;
import notkirill.workshopmanager.content.models.ContentJob;
import notkirill.workshopmanager.content.models.ItemContentRevision;
import org.springframework.stereotype.Component;


@Component
public class ContentResponseMapper {
    public ContentRevisionResponse toResponse(ItemContentRevision revision) {
        return new ContentRevisionResponse(
                revision.getRevision(),
                revision.getFileName(),
                revision.getFileSize(),
                revision.getSha256(),
                revision.getSourceVersion(),
                revision.getUploadedByUserId() != null,
                revision.getTimeCreated()
        );
    }

    public ContentSummaryResponse toSummary(ContentService.Summary summary) {
        return new ContentSummaryResponse(summary.maxRevisions(),
                summary.revisions().stream().map(this::toResponse).toList(),
                toJob(summary.activeJob()), toJob(summary.lastFailedJob()));
    }

    private ContentSummaryResponse.Job toJob(@Nullable ContentJob job) {
        if (job == null) {
            return null;
        }
        return new ContentSummaryResponse.Job(
                job.getId(),
                job.getType(),
                job.getStatus(),
                job.getAttempts(),
                job.getNextAttemptAt(),
                job.getErrorCode()
        );
    }
}
