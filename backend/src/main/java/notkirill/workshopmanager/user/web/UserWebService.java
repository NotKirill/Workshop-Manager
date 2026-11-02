package notkirill.workshopmanager.user.web;

import notkirill.workshopmanager.exception.BadRequestException;
import notkirill.workshopmanager.exception.ForbiddenException;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.ott.OneTimeTokenService;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.security.auth.provider.steam.ApprovedSteamLinkPayload;
import notkirill.workshopmanager.security.auth.provider.steam.SteamOpenIdService;
import notkirill.workshopmanager.user.User;
import notkirill.workshopmanager.user.UserAccountService;
import notkirill.workshopmanager.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserWebService {
    private final UserService userService;
    private final UserAccountService accountService;
    private final PersonalItemService personalItemService;
    private final OneTimeTokenService oneTimeTokenService;
    private final SteamOpenIdService steamOpenIdService;

    @Autowired
    public UserWebService(UserService userService, UserAccountService accountService,
                          PersonalItemService personalItemService, OneTimeTokenService oneTimeTokenService,
                          SteamOpenIdService steamOpenIdService) {
        this.userService = userService;
        this.accountService = accountService;
        this.personalItemService = personalItemService;
        this.oneTimeTokenService = oneTimeTokenService;
        this.steamOpenIdService = steamOpenIdService;
    }

    public ProfileResponse getCurrentUser(UserPrincipal principal) {
        User user = userService.require(principal.getUserId());
        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getTimeCreated(),
                user.getPasswordHash() != null,
                userService.getPermissions(user).stream().map(PermissionInfoResponse::of).toList(),
                userService.getIdentities(user.getId()).stream()
                        .map(identity -> new IdentityResponse(identity.getProvider(), identity.getProviderKey()))
                        .toList(),
                personalItemService.countOfUser(user.getId())
        );
    }

    public ProfileResponse rename(UserPrincipal principal, String name) {
        accountService.rename(principal.getUserId(), name);
        return getCurrentUser(principal);
    }

    public void changePassword(UserPrincipal principal, ChangePasswordRequest request) {
        accountService.changeOwnPassword(
                principal.getUserId(), request.currentPassword(), request.newPassword(),
                request.refreshToken()
        );
    }

    public ProfileResponse setLocalIdentity(UserPrincipal principal, AuthSource provider, String key) {
        if (provider == AuthSource.STEAM) {
            throw new BadRequestException("STEAM_LINK_REQUIRED", "Steam accounts are linked through Steam sign-in");
        }
        accountService.setIdentity(principal.getUserId(), provider, key);
        return getCurrentUser(principal);
    }

    public String createSteamLinkUrl(UserPrincipal principal) {
        return steamOpenIdService.constructLinkRedirect(principal.getUserId());
    }

    public ProfileResponse linkSteam(UserPrincipal principal, String ott) {
        ApprovedSteamLinkPayload payload = oneTimeTokenService.consumeToken(ott, ApprovedSteamLinkPayload.class);
        if (payload.userId() != principal.getUserId()) {
            throw new ForbiddenException("STEAM_LINK_FOREIGN", "This Steam link was started by another user");
        }
        accountService.setIdentity(principal.getUserId(), AuthSource.STEAM, payload.steamId());
        return getCurrentUser(principal);
    }

    public ProfileResponse removeIdentity(UserPrincipal principal, AuthSource provider) {
        accountService.removeIdentity(principal.getUserId(), provider);
        return getCurrentUser(principal);
    }
}
