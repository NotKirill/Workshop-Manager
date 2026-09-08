package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.UnauthorizedException;

public class RefreshTokenReusedException extends UnauthorizedException {
    public RefreshTokenReusedException() {
        super("REFRESH_TOKEN_REUSED", "Refresh token has reused.");
    }
}