package notkirill.workshopmanager.game.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.util.List;

public record GameResponse(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("slug") String slug,
        @JsonProperty("name_pending") boolean namePending,
        @JsonProperty("cover_url") String coverUrl,
        @JsonProperty("modspace_ids") List<ModspaceId> modspaceIds,
        @JsonProperty("compatible_game_ids") List<Long> compatibleGameIds,
        @JsonProperty("item_count") long itemCount
) {
    public record ModspaceId(
            @JsonProperty("modspace") ModspaceType modspace,
            @JsonProperty("external_id") String externalId
    ) {
    }
}
