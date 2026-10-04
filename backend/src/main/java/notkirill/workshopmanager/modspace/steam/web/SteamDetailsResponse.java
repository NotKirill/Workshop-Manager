package notkirill.workshopmanager.modspace.steam.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record SteamDetailsResponse(
        @JsonProperty("published_file_id") String publishedFileId,
        @JsonProperty("consumer_app_id") long consumerAppId,
        @JsonProperty("creator_app_id") long creatorAppId,
        @JsonProperty("visibility") short visibility,
        @JsonProperty("subscriptions") int subscriptions,
        @JsonProperty("favorited") int favorited,
        @JsonProperty("lifetime_subscriptions") int lifetimeSubscriptions,
        @JsonProperty("lifetime_favorited") int lifetimeFavorited,
        @JsonProperty("votes_up") Integer votesUp,
        @JsonProperty("votes_down") Integer votesDown,
        @JsonProperty("score") Float score,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("previews") List<Preview> previews,
        @JsonProperty("links") List<Link> links,
        @JsonProperty("content_descriptor_ids") short[] contentDescriptorIds,
        @JsonProperty("maybe_inappropriate_sex") boolean maybeInappropriateSex,
        @JsonProperty("maybe_inappropriate_violence") boolean maybeInappropriateViolence,
        @JsonProperty("short_description") String shortDescription
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SteamDetailsResponse that = (SteamDetailsResponse) o;
        return favorited == that.favorited && visibility == that.visibility && creatorAppId == that.creatorAppId && subscriptions == that.subscriptions && consumerAppId == that.consumerAppId && lifetimeFavorited == that.lifetimeFavorited && lifetimeSubscriptions == that.lifetimeSubscriptions && maybeInappropriateSex == that.maybeInappropriateSex && maybeInappropriateViolence == that.maybeInappropriateViolence && Objects.equals(
                score, that.score) && Objects.equals(votesUp, that.votesUp) && Objects.equals(
                links, that.links) && Objects.equals(votesDown, that.votesDown) && Objects.equals(
                tags, that.tags) && Objects.equals(
                publishedFileId, that.publishedFileId) && Objects.equals(
                previews, that.previews) && Objects.equals(
                shortDescription, that.shortDescription) && Objects.deepEquals(
                contentDescriptorIds, that.contentDescriptorIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                publishedFileId, consumerAppId, creatorAppId, visibility, subscriptions, favorited,
                lifetimeSubscriptions,
                lifetimeFavorited, votesUp, votesDown, score, tags, previews, links,
                Arrays.hashCode(contentDescriptorIds),
                maybeInappropriateSex, maybeInappropriateViolence, shortDescription
        );
    }

    @Override
    @NonNull
    public String toString() {
        return "SteamDetailsResponse{" +
                "publishedFileId='" + publishedFileId + '\'' +
                ", consumerAppId=" + consumerAppId +
                ", visibility=" + visibility +
                '}';
    }

    public record Preview(
            @JsonProperty("type") String type,
            @JsonProperty("url") String url,
            @JsonProperty("external_id") String externalId
    ) {
    }

    public record Link(
            @JsonProperty("key") String key,
            @JsonProperty("value") String value
    ) {
    }
}
