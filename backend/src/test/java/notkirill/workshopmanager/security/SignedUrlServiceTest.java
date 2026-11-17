package notkirill.workshopmanager.security;

import notkirill.workshopmanager.security.auth.jwt.JwtProperties;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class SignedUrlServiceTest {
    private static final JwtProperties JWT = new JwtProperties("mBAEd2D1Ce1d6nwGMepFnhOIpJwxQPJskBhcwIcAyW2", 300);

    @Test
    void acceptsValidSignatureOnlyForSameResource() {
        SignedUrlService service = new SignedUrlService(JWT, Clock.systemUTC());
        SignedUrlService.Signature signature = service.sign("content:1:2", Duration.ofMinutes(5));

        assertThat(service.verify("content:1:2", signature.expiresAt(), signature.value())).isTrue();
        assertThat(service.verify("content:1:3", signature.expiresAt(), signature.value())).isFalse();
        assertThat(service.verify("content:1:2", signature.expiresAt() + 1, signature.value())).isFalse();
        assertThat(service.verify("content:1:2", signature.expiresAt(), signature.value() + "x")).isFalse();
    }

    @Test
    void rejectsExpiredSignature() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        SignedUrlService signer = new SignedUrlService(JWT, Clock.fixed(now, ZoneOffset.UTC));
        SignedUrlService.Signature signature = signer.sign("media:7", Duration.ofMinutes(1));

        SignedUrlService later = new SignedUrlService(JWT, Clock.fixed(now.plusSeconds(120), ZoneOffset.UTC));
        assertThat(later.verify("media:7", signature.expiresAt(), signature.value())).isFalse();
    }

    @Test
    void cacheableSignatureStaysTheSameWithinItsWindow() {
        Instant windowStart = Instant.parse("2026-01-01T10:00:00Z");
        Duration ttl = Duration.ofHours(1);

        SignedUrlService.Signature first = signerAt(windowStart).signCacheable("media:7", ttl);
        SignedUrlService.Signature last = signerAt(windowStart.plusSeconds(3599)).signCacheable("media:7", ttl);
        SignedUrlService.Signature next = signerAt(windowStart.plusSeconds(3600)).signCacheable("media:7", ttl);

        assertThat(last).isEqualTo(first);
        assertThat(first.expiresAt()).isEqualTo(Instant.parse("2026-01-01T12:00:00Z").getEpochSecond());
        assertThat(next).isNotEqualTo(first);
        assertThat(signerAt(windowStart.plusSeconds(3599)).verify("media:7", first.expiresAt(), first.value()))
                .isTrue();
    }

    private static SignedUrlService signerAt(Instant now) {
        return new SignedUrlService(JWT, Clock.fixed(now, ZoneOffset.UTC));
    }
}
