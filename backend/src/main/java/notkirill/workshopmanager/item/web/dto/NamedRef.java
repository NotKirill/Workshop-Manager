package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NamedRef(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name
) {
}
