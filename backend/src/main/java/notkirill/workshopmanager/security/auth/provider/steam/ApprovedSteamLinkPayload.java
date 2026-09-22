package notkirill.workshopmanager.security.auth.provider.steam;

import com.fasterxml.jackson.annotation.JsonTypeName;
import notkirill.workshopmanager.ott.TokenPayload;

@JsonTypeName("APPROVED_STEAM_LINK")
public record ApprovedSteamLinkPayload(long userId, String steamId) implements TokenPayload {
    @Override
    public String getType() {
        return "APPROVED_STEAM_LINK";
    }
}
