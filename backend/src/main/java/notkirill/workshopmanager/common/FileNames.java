package notkirill.workshopmanager.common;

import jakarta.annotation.Nullable;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class FileNames {
    private static final int MAX_BYTES = 200;
    private static final Pattern UNSAFE = Pattern.compile("[^\\p{L}\\p{M}\\p{N} ._()\\[\\],+!'-]");
    private static final Pattern TRAILING_DOTS_AND_SPACES = Pattern.compile("[. ]+$");

    private FileNames() {
    }

    public static String sanitize(@Nullable String name, String fallback) {
        if (name == null) {
            return fallback;
        }
        String segment = name.replace('\\', '/');
        segment = segment.substring(segment.lastIndexOf('/') + 1);
        String cleaned = UNSAFE.matcher(Normalizer.normalize(segment, Normalizer.Form.NFC)).replaceAll("_");
        cleaned = keepEnd(cleaned).strip();
        cleaned = TRAILING_DOTS_AND_SPACES.matcher(cleaned).replaceAll("");
        return cleaned.isEmpty() ? fallback : cleaned;
    }

    private static String keepEnd(String value) {
        int bytes = 0;
        int start = value.length();
        while (start > 0) {
            int codePoint = value.codePointBefore(start);
            int size = codePoint < 0x80 ? 1 : codePoint < 0x800 ? 2 : codePoint < 0x10000 ? 3 : 4;
            if (bytes + size > MAX_BYTES) {
                break;
            }
            bytes += size;
            start -= Character.charCount(codePoint);
        }
        return value.substring(start);
    }
}
