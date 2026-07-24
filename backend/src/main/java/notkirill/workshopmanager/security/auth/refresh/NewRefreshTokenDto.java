package notkirill.workshopmanager.security.auth.refresh;

public record NewRefreshTokenDto(
        RefreshToken tokenEntity,
        String token
) {
}
