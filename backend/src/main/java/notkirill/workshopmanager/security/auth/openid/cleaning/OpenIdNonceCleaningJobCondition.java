package notkirill.workshopmanager.security.auth.openid.cleaning;

import notkirill.workshopmanager.security.auth.openid.OpenIdProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OpenIdNonceCleaningJobCondition implements Condition {
    @Override
    @SuppressWarnings("java:S2259")
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(OpenIdProperties.CONF_PATH, OpenIdProperties.class)
                .map(properties -> properties.nonce().cleaningJob().enabled())
                .orElse(false);
    }
}
