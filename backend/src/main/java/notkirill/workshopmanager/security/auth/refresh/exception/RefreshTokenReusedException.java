package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.ApiException;

public class RefreshTokenReusedException extends ApiException {
    public RefreshTokenReusedException() {
        super("REFRESH_TOKEN_REUSED", "Refresh token has reused.");
    }
}