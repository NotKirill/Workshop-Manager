package notkirill.workshopmanager.security.auth.jwt;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SecureJwtSecretKeyValidator.class)
public @interface SecureJwtSecretKey {
    String message() default "The secret key must contain at least 256 bits (32 bytes when decoded from Base64)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}