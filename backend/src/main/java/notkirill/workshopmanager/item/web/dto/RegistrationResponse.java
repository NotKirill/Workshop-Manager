package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import notkirill.workshopmanager.item.registration.RegistrationResult;
import notkirill.workshopmanager.modspace.ModspaceType;

import java.util.List;

public record RegistrationResponse(
        @JsonProperty("results") List<Result> results
) {
    public record Result(
            @JsonProperty("modspace") ModspaceType modspace,
            @JsonProperty("original_id") String originalId,
            @JsonProperty("status") RegistrationResult.Status status,
            @JsonProperty("personal_id") Long personalId,
            @JsonProperty("internal_id") Long internalId,
            @JsonProperty("title") String title,
            @JsonProperty("reason") String reason,
            @JsonProperty("dependency_of") Long dependencyOf
    ) {
    }
}
