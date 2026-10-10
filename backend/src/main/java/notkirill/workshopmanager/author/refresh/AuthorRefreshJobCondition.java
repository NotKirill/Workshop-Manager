package notkirill.workshopmanager.author.refresh;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AuthorRefreshJobCondition implements Condition {
    @Override
    @SuppressWarnings("java:S2259")
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(AuthorRefreshProperties.CONF_PATH, AuthorRefreshProperties.class)
                .map(AuthorRefreshProperties::enabled)
                .orElse(true);
    }
}
