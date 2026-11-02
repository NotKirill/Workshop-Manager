package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import notkirill.workshopmanager.item.registration.ItemRegistrationService;

import java.util.List;

public record ReviewDependenciesRequest(
        @JsonProperty("add") @NotNull @Size(max = ItemRegistrationService.MAX_ITEMS_PER_REQUEST)
        List<@NotBlank @Size(max = 100) String> add,
        @JsonProperty("remove") @NotNull @Size(max = 500) List<@NotNull Long> remove
) {
}
