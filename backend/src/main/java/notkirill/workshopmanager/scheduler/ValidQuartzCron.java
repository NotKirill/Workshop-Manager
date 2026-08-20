package notkirill.workshopmanager.scheduler;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuartzCronValidator.class)
public @interface ValidQuartzCron {
    String message() default "Invalid quartz cron expression";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}