package notkirill.workshopmanager.media.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MediaEmbedRequest(
        @JsonProperty("url") @NotBlank @Size(max = 2000) String url,
        @JsonProperty("purpose") @NotBlank String purpose
) {
}
