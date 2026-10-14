package notkirill.workshopmanager.security;

import notkirill.workshopmanager.security.auth.jwt.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class SignedUrlService {
    private final byte[] key;
    private final Clock clock;

    @Autowired
    public SignedUrlService(JwtProperties jwtProperties, Clock clock, MessageDigest messageDigest) {
        this.key = messageDigest.digest(("signed-url:" + jwtProperties.secretKey()).getBytes(StandardCharsets.UTF_8));
        this.clock = clock;
    }

    public Signature sign(String resource, Duration ttl) {
        long expiresAt = Instant.now(clock).plus(ttl).getEpochSecond();
        return new Signature(expiresAt, hmac(resource + "|" + expiresAt));
    }

    public Signature signCacheable(String resource, Duration ttl) {
        long window = ttl.toSeconds();
        long expiresAt = (Math.floorDiv(Instant.now(clock).getEpochSecond(), window) + 2) * window;
        return new Signature(expiresAt, hmac(resource + "|" + expiresAt));
    }

    public boolean verify(String resource, long expiresAt, String signature) {
        if (signature == null || Instant.now(clock).getEpochSecond() > expiresAt) {
            return false;
        }
        byte[] expected = hmac(resource + "|" + expiresAt).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.US_ASCII));
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public record Signature(long expiresAt, String value) {
    }
}
