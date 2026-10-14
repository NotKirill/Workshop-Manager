package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class FileNamesTest {
    @Test
    void keepsLettersOfEveryScript() {
        String name = FileNames.sanitize("Мой мод 模组 (v2).zip", "fallback");

        assertThat(name).isEqualTo("Мой мод 模组 (v2).zip");
    }

    @Test
    void replacesInvisibleAndControlCharacters() {
        // Right-to-left override would show "gnp.exe" as "exe.png"; zero-width space and a line break
        String name = FileNames.sanitize("photo‮gnp.exe​\nend", "fallback");

        assertThat(name).isEqualTo("photo_gnp.exe__end");
    }

    @Test
    void namesOfOnlyDotsBecomeTheFallback() {
        assertThat(FileNames.sanitize("..", "fallback")).isEqualTo("fallback");
        assertThat(FileNames.sanitize("a/b/..", "fallback")).isEqualTo("fallback");
        assertThat(FileNames.sanitize("name. . ", "fallback")).isEqualTo("name");
    }

    @Test
    void limitsBytesKeepingTheEnd() {
        String name = FileNames.sanitize("я".repeat(300) + ".zip", "fallback");

        assertThat(name.getBytes(StandardCharsets.UTF_8).length).isLessThanOrEqualTo(200);
        assertThat(name).endsWith("яя.zip");
    }

    @Test
    void composesDecomposedLetters() {
        // "й" as "и" plus a combining breve, as macOS sends it
        String name = FileNames.sanitize("мойй.zip", "fallback");

        assertThat(name).isEqualTo("мойй.zip");
    }
}
