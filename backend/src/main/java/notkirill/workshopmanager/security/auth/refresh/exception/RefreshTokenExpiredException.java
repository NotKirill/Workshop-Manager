package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.UnauthorizedException;

public class RefreshTokenExpiredException extends UnauthorizedException {
    public RefreshTokenExpiredException() {
        super("REFRESH_TOKEN_EXPIRED", "Refresh token has expired.");
    }
}