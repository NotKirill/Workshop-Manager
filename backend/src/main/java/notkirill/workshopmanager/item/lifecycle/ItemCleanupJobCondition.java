package notkirill.workshopmanager.item.lifecycle;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ItemCleanupJobCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(ItemCleanupProperties.CONF_PATH, ItemCleanupProperties.class)
                .map(ItemCleanupProperties::enabled)
                .orElse(true);
    }
}
