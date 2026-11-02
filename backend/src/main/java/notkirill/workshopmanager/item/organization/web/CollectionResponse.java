package notkirill.workshopmanager.item.organization.web;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CollectionResponse(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("game_id") long gameId,
        @JsonProperty("item_count") long itemCount
) {
}
