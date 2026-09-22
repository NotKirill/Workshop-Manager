package notkirill.workshopmanager.security.auth.provider.steam;

import com.fasterxml.jackson.annotation.JsonTypeName;
import notkirill.workshopmanager.ott.TokenPayload;

@JsonTypeName("STEAM_LINK_REQUEST")
public record SteamLinkRequestPayload(long userId) implements TokenPayload {
    @Override
    public String getType() {
        return "STEAM_LINK_REQUEST";
    }
}
