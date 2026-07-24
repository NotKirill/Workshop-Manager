package notkirill.workshopmanager.security.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    @Autowired
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserDetails userDetails) {
        JwtPayload payload = new JwtPayload(
                userDetails.getUsername(),
                new Date(),
                Date.from(Instant.now().plus(jwtProperties.expirationSeconds(), ChronoUnit.SECONDS))
        );
        return buildToken(payload);
    }

    public JwtPayload extractPayload(String token) {
        Claims claims = extractAllClaims(token);

        return new JwtPayload(
                claims.getSubject(),
                claims.getIssuedAt(),
                claims.getExpiration()
        );
    }

    public boolean isTokenValid(JwtPayload payload, UserDetails userDetails) {
        return (payload.sub().equals(userDetails.getUsername())) && !isTokenExpired(payload);
    }

    private String buildToken(JwtPayload payload) {
        return Jwts
                .builder()
                .subject(payload.sub())
                .issuedAt(payload.iat())
                .expiration(payload.exp())
                .signWith(getSigningKey())
                .compact();
    }

    private boolean isTokenExpired(JwtPayload payload) {
        return payload.exp().before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
