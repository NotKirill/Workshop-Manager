package notkirill.workshopmanager.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class DatabaseLocks {
    private final JdbcTemplate jdbcTemplate;
    private final MessageDigest messageDigest;

    @Autowired
    public DatabaseLocks(JdbcTemplate jdbcTemplate, MessageDigest messageDigest) {
        this.jdbcTemplate = jdbcTemplate;
        this.messageDigest = messageDigest;
    }

    long toLockId(String key) {
        byte[] digest = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
        long value = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            value = (value << 8) | (digest[i] & 0xFF);
        }
        return value;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void lock(String key) {
        jdbcTemplate.query("SELECT pg_advisory_xact_lock(?)", rs -> null, toLockId(key));
    }
}
