package notkirill.workshopmanager.security.auth.jwt;

import java.util.Date;

public record JwtPayload(
        String sub,
        Date iat,
        Date exp
) {
}