package notkirill.workshopmanager.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Component
public class DatabaseLocks {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseLocks(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    static long toLockId(String key) {
        byte[] digest = Hashes.sha256(key.getBytes(StandardCharsets.UTF_8));
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
