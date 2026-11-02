package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BulkResultResponse(
        @JsonProperty("affected") int affected
) {
}
