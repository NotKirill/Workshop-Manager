package notkirill.workshopmanager.media.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MediaLinkRequest(
        @JsonProperty("media_item_id") @NotNull Long mediaItemId,
        @JsonProperty("purpose") @NotBlank String purpose
) {
}
