package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.content.web.ContentSummaryResponse;
import notkirill.workshopmanager.item.enums.ItemSourceStatus;
import notkirill.workshopmanager.item.enums.RelationType;
import notkirill.workshopmanager.media.web.MediaResponse;
import notkirill.workshopmanager.modspace.ModspaceType;
import notkirill.workshopmanager.modspace.steam.web.SteamDetailsResponse;

import java.time.OffsetDateTime;
import java.util.List;

public record ItemDetailResponse(
        @JsonProperty("id") long id,
        @JsonProperty("internal_id") long internalId,
        @JsonProperty("modspace") ModspaceType modspace,
        @JsonProperty("original_id") String originalId,
        @JsonProperty("source_status") ItemSourceStatus sourceStatus,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("title_overridden") boolean titleOverridden,
        @JsonProperty("description_overridden") boolean descriptionOverridden,
        @JsonProperty("original") Original original,
        @JsonProperty("personal") Personal personal,
        @JsonProperty("game") Game game,
        @JsonProperty("author") Author author,
        @JsonProperty("file_size") Long fileSize,
        @JsonProperty("source_time_created") OffsetDateTime sourceTimeCreated,
        @JsonProperty("source_time_updated") OffsetDateTime sourceTimeUpdated,
        @JsonProperty("time_synced") OffsetDateTime timeSynced,
        @JsonProperty("rating") Short rating,
        @JsonProperty("tags") List<NamedRef> tags,
        @JsonProperty("groups") List<NamedRef> groups,
        @JsonProperty("relations") List<Relation> relations,
        @JsonProperty("dependencies") List<Dependency> dependencies,
        @JsonProperty("steam") SteamDetailsResponse steam,
        @JsonProperty("content") Content content,
        @JsonProperty("media") List<MediaResponse> media
) {
    public record Original(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description
    ) {
    }

    public record Personal(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("user_commentary") String userCommentary,
            @JsonProperty("auto_backup") boolean autoBackup,
            @JsonProperty("time_registered") OffsetDateTime timeRegistered,
            // null: dependencies are not tracked; true: changed since the last review
            @JsonProperty("dependencies_need_review") Boolean dependenciesNeedReview
    ) {
    }

    public record Game(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name
    ) {
    }

    public record Author(
            @JsonProperty("id") long id,
            @JsonProperty("name") String name
    ) {
    }

    // direction OUTGOING (this item -> other) or INCOMING (other -> this item)
    public record Relation(
            @JsonProperty("item_id") long itemId,
            @JsonProperty("title") String title,
            @JsonProperty("relation") RelationType relation,
            @JsonProperty("direction") String direction
    ) {
    }

    public record Dependency(
            @JsonProperty("original_id") String originalId,
            @JsonProperty("personal_id") Long personalId,
            @JsonProperty("title") String title
    ) {
    }

    public record Content(
            @JsonProperty("summary") ContentSummaryResponse summary,
            @JsonProperty("can_backup") boolean canBackup,
            @JsonProperty("can_upload") boolean canUpload,
            @JsonProperty("can_delete") boolean canDelete
    ) {
    }
}
