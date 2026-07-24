package notkirill.workshopmanager.security.auth.jwt;

import io.jsonwebtoken.io.Decoders;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SecureJwtSecretKeyValidator implements ConstraintValidator<SecureJwtSecretKey, String> {
    @Override
    public boolean isValid(String secretKey, ConstraintValidatorContext context) {
        if (secretKey == null) {
            return true;
        }

        try {
            return Decoders.BASE64.decode(secretKey).length >= 32;
        } catch (Exception ex) {
            return false;
        }
    }
}