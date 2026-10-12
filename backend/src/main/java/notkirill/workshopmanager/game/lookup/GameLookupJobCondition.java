package notkirill.workshopmanager.game.lookup;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class GameLookupJobCondition implements Condition {
    @Override
    public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(GameLookupProperties.CONF_PATH, GameLookupProperties.class)
                .map(GameLookupProperties::enabled)
                .orElse(true);
    }
}
