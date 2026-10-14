package notkirill.workshopmanager.media.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.media.enums.MediaProvider;
import notkirill.workshopmanager.media.enums.MediaType;

public record MediaResponse(
        @JsonProperty("id") long linkId,
        @JsonProperty("media_item_id") long mediaItemId,
        @JsonProperty("type") MediaType type,
        @JsonProperty("url") String url,
        @JsonProperty("provider") MediaProvider provider,
        @JsonProperty("external_id") String externalId,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("purpose") String purpose,
        @JsonProperty("sort_order") int sortOrder,
        @JsonProperty("owned") boolean owned,
        @JsonProperty("hidden") boolean hidden
) {
}
