package notkirill.workshopmanager.modspace.steam;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SteamOriginalIdTest {
    private final SteamModspaceAdapter adapter = new SteamModspaceAdapter(null, null, null, null, null, null, null,
            mock(PlatformTransactionManager.class));

    @Test
    void acceptsPublishedFileIds() {
        assertThat(adapter.isValidOriginalId("105982362")).isTrue();
        assertThat(adapter.isValidOriginalId("18446744073709551615")).isTrue();
    }

    @Test
    void rejectsLinksAndOtherText() {
        assertThat(adapter.isValidOriginalId("https://steamcommunity.com/sharedfiles/filedetails/?id=105982362"))
                .isFalse();
        assertThat(adapter.isValidOriginalId("not-an-id")).isFalse();
        assertThat(adapter.isValidOriginalId("")).isFalse();
        assertThat(adapter.isValidOriginalId("123456789012345678901")).isFalse();
    }
}
