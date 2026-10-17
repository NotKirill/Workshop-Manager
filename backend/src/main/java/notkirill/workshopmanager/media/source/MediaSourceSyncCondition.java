package notkirill.workshopmanager.media.source;

import notkirill.workshopmanager.media.MediaProperties;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MediaSourceSyncCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind("application.media", MediaProperties.class)
                .map(MediaProperties::syncEnabled)
                .orElse(true);
    }
}
