package notkirill.workshopmanager.security.auth;

import org.springframework.stereotype.Service;

@Service
public class AuthValidator {
    public void validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (email.length() < 3 || email.length() > 254) {
            throw new IllegalArgumentException("Email must be between 3 and 254 characters");
        }

        if (!email.matches(
                "^(?!\\.)(?!.*\\.\\.)([a-z0-9_'+\\-\\.]*)[a-z0-9_+\\-]@([a-z0-9][a-z0-9\\-]*\\.)+[a-z]{2,}$")) {
            throw new IllegalArgumentException("Please enter a valid email address");
        }
    }

    public void validateUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }

        if (!username.matches("^[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*$")) {
            throw new IllegalArgumentException(
                    "Username may only contain alphanumeric characters or single hyphens, and cannot begin or end with a hyphen");
        }
    }

    public void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 6 || password.length() > 128) {
            throw new IllegalArgumentException("Password must be between 6 and 128 characters");
        }
    }
}
