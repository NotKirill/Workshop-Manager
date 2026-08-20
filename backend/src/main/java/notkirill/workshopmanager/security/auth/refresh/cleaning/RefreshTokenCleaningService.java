package notkirill.workshopmanager.security.auth.refresh.cleaning;

import notkirill.workshopmanager.security.auth.refresh.RefreshTokenProperties;
import notkirill.workshopmanager.security.auth.refresh.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.Period;

@Service
public class RefreshTokenCleaningService {
    private final RefreshTokenProperties properties;
    private final RefreshTokenRepository repository;

    private final Clock clock;

    @Autowired
    public RefreshTokenCleaningService(RefreshTokenProperties properties, RefreshTokenRepository repository,
                                       Clock clock) {
        this.properties = properties;
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public long clearOldExpiredTokens() {
        return repository.deleteByExpiresAtLessThan(OffsetDateTime.now(clock).minus(
                Period.ofDays(properties.cleaningJob().retentionDaysAfterExpiration())));
    }
}
