package notkirill.workshopmanager.user.web;

import notkirill.workshopmanager.exception.ForbiddenException;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.security.auth.provider.steam.ApprovedSteamLinkPayload;
import notkirill.workshopmanager.security.auth.provider.steam.SteamOpenIdService;
import notkirill.workshopmanager.user.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserWebService {
    private final UserAccountService accountService;
    private final OneTimeTokenService oneTimeTokenService;
    private final SteamOpenIdService steamOpenIdService;

    @Autowired
    public UserWebService(UserAccountService accountService, OneTimeTokenService oneTimeTokenService,
                          SteamOpenIdService steamOpenIdService) {
        this.accountService = accountService;
        this.oneTimeTokenService = oneTimeTokenService;
        this.steamOpenIdService = steamOpenIdService;
    }

    public String createSteamLinkUrl(UserPrincipal principal) {
        return steamOpenIdService.constructLinkRedirect(principal.getUserId());
    }

    public void linkSteam(UserPrincipal principal, String ott) {
        ApprovedSteamLinkPayload payload = oneTimeTokenService.consumeToken(ott, ApprovedSteamLinkPayload.class);
        if (payload.userId() != principal.getUserId()) {
            throw new ForbiddenException("STEAM_LINK_FOREIGN", "This Steam link was started by another user");
        }
        accountService.setIdentity(principal.getUserId(), AuthSource.STEAM, payload.steamId());
    }
}
