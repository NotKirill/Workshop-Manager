package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseLocksTest {
    @Test
    void lockIdsAreStableAndDistinct() throws NoSuchAlgorithmException {
        DatabaseLocks locks = new DatabaseLocks(null, MessageDigest.getInstance("SHA-256"));

        assertThat(locks.toLockId("internal:STEAM:1")).isEqualTo(locks.toLockId("internal:STEAM:1"));
        assertThat(locks.toLockId("internal:STEAM:1")).isNotEqualTo(locks.toLockId("internal:STEAM:2"));
    }
}
