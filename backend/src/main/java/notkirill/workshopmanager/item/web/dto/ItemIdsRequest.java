package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ItemIdsRequest(
        @JsonProperty("item_ids") @NotEmpty @Size(max = 1000) List<@NotNull Long> itemIds
) {
}
