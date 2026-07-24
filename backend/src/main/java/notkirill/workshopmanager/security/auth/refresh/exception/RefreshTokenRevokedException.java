package notkirill.workshopmanager.security.auth.refresh.exception;

import notkirill.workshopmanager.exception.ApiException;

public class RefreshTokenRevokedException extends ApiException {
    public RefreshTokenRevokedException() {
        super("REFRESH_TOKEN_REVOKED", "Refresh token has revoked.");
    }
}