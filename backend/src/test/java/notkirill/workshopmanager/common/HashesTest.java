package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class HashesTest {
    private static final String ABC_HASH = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

    @Test
    void textBytesAndFileGiveTheSameStandardHash(@TempDir Path dir) throws IOException {
        Path file = Files.writeString(dir.resolve("abc.txt"), "abc", StandardCharsets.UTF_8);

        String ofText = Hashes.sha256Hex("abc");
        String ofBytes = HexFormat.of().formatHex(Hashes.sha256("abc".getBytes(StandardCharsets.UTF_8)));
        String ofFile = Hashes.sha256Hex(file);

        assertThat(ofText).isEqualTo(ABC_HASH);
        assertThat(ofBytes).isEqualTo(ABC_HASH);
        assertThat(ofFile).isEqualTo(ABC_HASH);
    }

    @Test
    void parallelUseGivesCorrectHashes() throws InterruptedException {
        String expected = Hashes.sha256Hex("token-value");
        boolean[] wrong = {false};
        Thread[] threads = new Thread[8];
        for (int t = 0; t < threads.length; t++) {
            threads[t] = Thread.ofPlatform().start(() -> {
                for (int i = 0; i < 20_000; i++) {
                    if (!Hashes.sha256Hex("token-value").equals(expected)) {
                        wrong[0] = true;
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.join();
        }

        assertThat(wrong[0]).isFalse();
    }
}
