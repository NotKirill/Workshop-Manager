package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.UnauthorizedException;

public class RefreshTokenRevokedException extends UnauthorizedException {
    public RefreshTokenRevokedException() {
        super("REFRESH_TOKEN_REVOKED", "Refresh token has revoked.");
    }
}