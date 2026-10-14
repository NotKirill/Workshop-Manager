package notkirill.workshopmanager.media;

import notkirill.workshopmanager.exception.BadRequestException;

import java.util.Set;
import java.util.regex.Pattern;

public final class MediaPurposes {
    public static final String PREVIEW = "preview";
    public static final String SHOWCASE = "showcase";
    public static final String COVER = "cover";

    private static final Set<String> SINGLE = Set.of(PREVIEW, COVER);
    private static final Pattern FORMAT = Pattern.compile("[a-z0-9_-]{1,32}");

    private MediaPurposes() {
    }

    public static boolean isSingle(String purpose) {
        return SINGLE.contains(purpose);
    }

    public static void requireValid(String purpose) {
        if (!FORMAT.matcher(purpose).matches()) {
            throw new BadRequestException(
                    "INVALID_MEDIA_PURPOSE",
                    "Purpose must be 1-32 lowercase letters, digits, '_' or '-'"
            );
        }
    }
}
