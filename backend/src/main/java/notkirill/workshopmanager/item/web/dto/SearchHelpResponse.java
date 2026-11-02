package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SearchHelpResponse(
        @JsonProperty("fields") List<Field> fields,
        @JsonProperty("examples") List<String> examples
) {
    public record Field(
            @JsonProperty("tag") String tag,
            @JsonProperty("type") String type,
            @JsonProperty("sortable") boolean sortable,
            @JsonProperty("values") List<String> values
    ) {
    }
}
