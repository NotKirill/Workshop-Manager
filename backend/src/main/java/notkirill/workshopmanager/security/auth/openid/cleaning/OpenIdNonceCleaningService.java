package notkirill.workshopmanager.security.auth.openid.cleaning;

import notkirill.workshopmanager.security.auth.openid.OpenIdNonceRepository;
import notkirill.workshopmanager.security.auth.openid.OpenIdProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.Period;

@Service
public class OpenIdNonceCleaningService {
    private final OpenIdProperties properties;
    private final OpenIdNonceRepository repository;

    private final Clock clock;

    @Autowired
    public OpenIdNonceCleaningService(OpenIdProperties properties, OpenIdNonceRepository repository,
                                      Clock clock) {
        this.properties = properties;
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public long clearOldNonce() {
        return repository.deleteByRegisteredAtLessThan(OffsetDateTime.now(clock).minus(
                Period.ofDays(properties.nonce().cleaningJob().retentionDays())));
    }
}
