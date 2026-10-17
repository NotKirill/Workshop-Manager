package notkirill.workshopmanager.common;

import java.text.Normalizer;
import java.util.Locale;

public final class Texts {
    private Texts() {
    }

    public static String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.strip();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, Math.max(0, maxLength - 1)) + "…";
    }

    public static String errorMessage(Throwable throwable) {
        String message = throwable.getMessage();
        String text = throwable.getClass().getSimpleName() + (message != null ? ": " + message : "");
        return truncate(text, 2000);
    }

    public static String toSlug(String name) {
        String ascii = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String slug = ascii.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        return slug.isEmpty() ? "game" : slug;
    }
}
