package notkirill.workshopmanager.security.auth;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;

@Component
public class AuthNormalizer {
    public String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public String normalizeUsername(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase(Locale.ROOT);
    }

    public String normalizePassword(String password) {
        if (password == null) return null;
        return Normalizer.normalize(password, Normalizer.Form.NFC);
    }
}
