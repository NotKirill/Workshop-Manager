package notkirill.workshopmanager.content;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContentFileNameTest {
    @Test
    void stripsPathsAndUnsafeCharacters() {
        assertThat(ContentRevisionService.sanitizeFileName("../../etc/passwd")).isEqualTo("passwd");
        assertThat(ContentRevisionService.sanitizeFileName("C:\\mods\\my mod (v2).zip")).isEqualTo("my mod (v2).zip");
        assertThat(ContentRevisionService.sanitizeFileName("weird<>|name?.bin")).isEqualTo("weird___name_.bin");
        assertThat(ContentRevisionService.sanitizeFileName("   ")).isEqualTo("content.bin");
        assertThat(ContentRevisionService.sanitizeFileName(null)).isEqualTo("content.bin");
    }
}
