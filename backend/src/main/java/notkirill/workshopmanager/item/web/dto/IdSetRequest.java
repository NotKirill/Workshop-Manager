package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record IdSetRequest(
        @JsonProperty("ids") @NotNull @Size(max = 500) List<@NotNull Long> ids
) {
}
