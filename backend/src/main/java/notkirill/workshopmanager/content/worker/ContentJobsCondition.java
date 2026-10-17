package notkirill.workshopmanager.content.worker;

import notkirill.workshopmanager.content.ContentProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ContentJobsCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(ContentProperties.CONF_PATH, ContentProperties.class)
                .map(properties -> properties.jobs().enabled())
                .orElse(true);
    }
}
