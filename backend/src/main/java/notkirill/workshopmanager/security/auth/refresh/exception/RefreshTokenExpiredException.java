package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.ApiException;

public class RefreshTokenExpiredException extends ApiException {
    public RefreshTokenExpiredException() {
        super("REFRESH_TOKEN_EXPIRED", "Refresh token has expired.");
    }
}