package notkirill.workshopmanager.scheduler;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.scheduling.support.CronExpression;

public class SpringCronValidator implements ConstraintValidator<ValidSpringCron, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.isBlank()) {
            return false;
        }

        return CronExpression.isValidExpression(value);
    }
}