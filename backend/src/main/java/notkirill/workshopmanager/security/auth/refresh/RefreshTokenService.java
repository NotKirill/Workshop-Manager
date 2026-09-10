package notkirill.workshopmanager.security.auth.refresh;

import notkirill.workshopmanager.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenProperties properties;
    private final RefreshTokenRepository repository;

    private final SecureRandom secureRandom;
    private final MessageDigest messageDigest;
    private final Clock clock;

    @Autowired
    public RefreshTokenService(RefreshTokenProperties properties, RefreshTokenRepository repository,
                               SecureRandom secureRandom, MessageDigest messageDigest, Clock clock) {
        this.properties = properties;
        this.repository = repository;
        this.secureRandom = secureRandom;
        this.messageDigest = messageDigest;
        this.clock = clock;
    }

    public RefreshToken getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public RefreshToken getByToken(String token) {
        String tokenHash = getTokenHash(token);

        return repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("REFRESH_TOKEN_UNKNOWN", "Unknown refresh token"));
    }

    @Transactional
    public NewRefreshTokenDto create(Long userId, ClientInfo clientInfo) {
        return create(userId, UUID.randomUUID(), clientInfo);
    }

    private NewRefreshTokenDto create(Long userId, UUID familyId, ClientInfo clientInfo) {
        String token = generateToken();
        String tokenHash = getTokenHash(token);

        RefreshToken tokenEntity = new RefreshToken(
                userId, familyId, tokenHash,
                OffsetDateTime.now(clock).plusSeconds(properties.expirationSeconds()), clientInfo
        );
        repository.save(tokenEntity);

        return new NewRefreshTokenDto(tokenEntity, token);
    }

    public void validateToken(RefreshToken tokenEntity) {
        boolean expired = tokenEntity.getExpiresAt().isBefore(OffsetDateTime.now(clock));
        boolean revoked = tokenEntity.getRevokedAt() != null;
        boolean reused = tokenEntity.getReplacedBy() != null;

        if (expired) {
            throw new UnauthorizedException("REFRESH_TOKEN_EXPIRED", "Refresh token has expired.");
        }

        if (revoked) {
            if (reused) {
                revokeAllByFamilyId(tokenEntity.getFamilyId());
                throw new UnauthorizedException("REFRESH_TOKEN_REUSED", "Refresh token has reused.");
            }

            throw new UnauthorizedException("REFRESH_TOKEN_REVOKED", "Refresh token has revoked.");
        }
    }

    @Transactional
    public NewRefreshTokenDto rotateToken(RefreshToken tokenEntity, ClientInfo clientInfo) {
        validateToken(tokenEntity);
        NewRefreshTokenDto newTokenData = create(tokenEntity.getUserId(), tokenEntity.getFamilyId(), clientInfo);

        tokenEntity.setRevokedAt(OffsetDateTime.now(clock));
        tokenEntity.setReplacedBy(newTokenData.tokenEntity().getId());
        repository.save(tokenEntity);

        return newTokenData;
    }

    public void revokeToken(RefreshToken tokenEntity) {
        tokenEntity.setRevokedAt(OffsetDateTime.now(clock));
        repository.save(tokenEntity);
    }

    public void revokeAllByUserId(long userId) {
        repository.findByUserId(userId).forEach(this::revokeToken);
    }

    public void revokeOtherSessions(long userId, String currentToken) {
        UUID keptFamilyId = currentToken == null ? null : repository.findByTokenHash(getTokenHash(currentToken))
                .filter(token -> token.getUserId() == userId)
                .map(RefreshToken::getFamilyId)
                .orElse(null);
        repository.findByUserId(userId).stream()
                .filter(token -> token.getRevokedAt() == null && !token.getFamilyId().equals(keptFamilyId))
                .forEach(this::revokeToken);
    }

    public void deleteAllByUserId(long userId) {
        repository.deleteByUserId(userId);
    }

    public void revokeAllByFamilyId(UUID familyId) {
        repository.findByFamilyId(familyId).forEach(this::revokeToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String getTokenHash(String token) {
        return HexFormat.of().formatHex(messageDigest.digest(
                token.getBytes(StandardCharsets.UTF_8)));
    }
}
