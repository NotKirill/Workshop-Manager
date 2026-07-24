package notkirill.workshopmanager.security.auth.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("application.security.jwt")
@Validated
public record JwtProperties(
        @Name("secret-key") @NotNull @SecureJwtSecretKey String secretKey,
        @Name("expiration-seconds") @NotNull @Min(value = 1) Integer expirationSeconds) {
}
