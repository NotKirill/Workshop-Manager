package notkirill.workshopmanager.security.auth;

import notkirill.workshopmanager.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class AuthValidator {
    public void validateEmail(String email) {
        if (email == null) {
            throw new BadRequestException("INVALID_EMAIL", "Email cannot be empty");
        }

        if (email.length() < 3 || email.length() > 254) {
            throw new BadRequestException("INVALID_EMAIL", "Email must be between 3 and 254 characters");
        }

        if (!email.matches(
                "^(?!\\.)(?!.*\\.\\.)([a-z0-9_'+\\-\\.]*)[a-z0-9_+\\-]@([a-z0-9][a-z0-9\\-]*\\.)+[a-z]{2,}$")) {
            throw new BadRequestException("INVALID_EMAIL", "Please enter a valid email address");
        }
    }

    public void validateUsername(String username) {
        if (username == null) {
            throw new BadRequestException("INVALID_USERNAME", "Username cannot be empty");
        }

        if (username.length() < 3 || username.length() > 50) {
            throw new BadRequestException("INVALID_USERNAME", "Username must be between 3 and 50 characters");
        }

        if (!username.matches("^[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*$")) {
            throw new BadRequestException(
                    "INVALID_USERNAME", "Username may only contain alphanumeric characters or single hyphens,"
                    + " and cannot begin or end with a hyphen"
            );
        }
    }

    public void validatePasswordSignIn(String password) {
        if (password == null) {
            throw new BadRequestException("INVALID_PASSWORD_FORMAT", "Password cannot be empty");
        }

        if (password.length() < 8 || password.length() > 128) {
            throw new BadRequestException("INVALID_PASSWORD_FORMAT", "Password must be between 8 and 128 characters");
        }
    }

    public void validatePasswordSignUp(String password) {
        if (password == null) {
            throw new BadRequestException("WEAK_PASSWORD", "Password cannot be empty");
        }

        if (password.length() < 8 || password.length() > 128) {
            throw new BadRequestException("WEAK_PASSWORD", "Password must be between 8 and 128 characters");
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+}{';?>.<,]).*$")) {
            throw new BadRequestException(
                    "WEAK_PASSWORD", "Password must include uppercase, lowercase, a number, and a special character");
        }
    }
}
