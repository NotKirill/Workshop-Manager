package notkirill.workshopmanager.scheduler;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.quartz.CronExpression;

public class QuartzCronValidator implements ConstraintValidator<ValidQuartzCron, String> {
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