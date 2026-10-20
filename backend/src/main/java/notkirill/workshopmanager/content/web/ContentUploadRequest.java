package notkirill.workshopmanager.content.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContentUploadRequest(
        @JsonProperty("file_name") @NotBlank @Size(max = 500) String fileName,
        @JsonProperty("size") @NotNull Long size
) {
}
