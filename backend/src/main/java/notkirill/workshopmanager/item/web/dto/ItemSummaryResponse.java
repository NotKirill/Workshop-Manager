package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.content.enums.ContentJobType;
import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.time.OffsetDateTime;
import java.util.List;

public record ItemSummaryResponse(
        @JsonProperty("id") long id,
        @JsonProperty("internal_id") long internalId,
        @JsonProperty("title") String title,
        @JsonProperty("description_snippet") String descriptionSnippet,
        @JsonProperty("title_overridden") boolean titleOverridden,
        @JsonProperty("description_overridden") boolean descriptionOverridden,
        @JsonProperty("modspace") ModspaceType modspace,
        @JsonProperty("original_id") String originalId,
        @JsonProperty("game") NamedRef game,
        @JsonProperty("auto_backup") boolean autoBackup,
        @JsonProperty("rating") Short rating,
        @JsonProperty("source_status") ItemSourceStatus sourceStatus,
        @JsonProperty("file_size") Long fileSize,
        @JsonProperty("source_time_updated") OffsetDateTime sourceTimeUpdated,
        @JsonProperty("time_registered") OffsetDateTime timeRegistered,
        @JsonProperty("tags") List<NamedRef> tags,
        @JsonProperty("backup") Backup backup,
        @JsonProperty("preview_url") String previewUrl
) {
    public record Backup(
            @JsonProperty("revision_count") int revisionCount,
            @JsonProperty("latest_backup_at") OffsetDateTime latestBackupAt,
            @JsonProperty("active_job") ContentJobType activeJob
    ) {
    }
}
