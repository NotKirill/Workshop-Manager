package notkirill.workshopmanager.security.auth.openid;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface OpenIdNonceRepository extends JpaRepository<OpenIdNonce, OpenIdNonce.CompositeId> {
    Optional<OpenIdNonce> findByValueAndEndpoint(String value, String endpoint);

    boolean existsByValueAndEndpoint(String value, String endpoint);

    long deleteByValueAndEndpoint(String value, String endpoint);

    long deleteByRegisteredAtLessThan(OffsetDateTime registeredAt);
}