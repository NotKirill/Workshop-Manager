package notkirill.workshopmanager.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @JsonProperty("status") int status,
        @JsonProperty("code") String code,
        @JsonProperty("field_errors") Map<String, String> fieldErrors,
        @JsonProperty("error_uuid") String errorUuid,
        @JsonProperty("api_path") String apiPath,
        @JsonProperty("timestamp") OffsetDateTime timestamp
) {
}
