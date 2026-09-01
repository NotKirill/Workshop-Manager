package notkirill.workshopmanager.security.auth.refresh.cleaning;

import notkirill.workshopmanager.security.auth.refresh.RefreshTokenProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RefreshTokenCleaningJobCondition implements Condition {
    @Override
    @SuppressWarnings("java:S2259")
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(RefreshTokenProperties.CONF_PATH, RefreshTokenProperties.class)
                .map(properties -> properties.cleaningJob().enabled())
                .orElse(false);
    }
}
