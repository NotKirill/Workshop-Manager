package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseLocksTest {
    @Test
    void lockIdsAreStableAndDistinct() {
        assertThat(DatabaseLocks.toLockId("internal:STEAM:1")).isEqualTo(DatabaseLocks.toLockId("internal:STEAM:1"));
        assertThat(DatabaseLocks.toLockId("internal:STEAM:1")).isNotEqualTo(DatabaseLocks.toLockId("internal:STEAM:2"));
    }
}
