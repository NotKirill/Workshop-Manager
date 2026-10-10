package notkirill.workshopmanager.item.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import notkirill.workshopmanager.common.Texts;

public record LocalItemRegisteringRequest(
        @JsonProperty("game_id") Long gameId,
        @JsonProperty("game_name") @Size(max = 200) String gameName,
        @JsonProperty("title") @NotBlank @Size(max = 500) String title,
        @JsonProperty("description") @Size(max = 100_000) String description,
        @JsonProperty("user_commentary") @Size(max = 100_000) String userCommentary
) {
    @Override
    public String title() {
        return Texts.blankToNull(title);
    }

    @Override
    public String description() {
        return Texts.blankToNull(description);
    }

    @Override
    public String userCommentary() {
        return Texts.blankToNull(userCommentary);
    }

    @JsonIgnore
    @AssertTrue
    public boolean isGameGiven() {
        return gameId != null || (gameName != null && !gameName.isBlank());
    }
}
