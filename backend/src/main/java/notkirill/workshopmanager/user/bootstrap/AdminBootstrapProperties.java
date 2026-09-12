package notkirill.workshopmanager.user.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@ConfigurationProperties("application.bootstrap.admin")
public record AdminBootstrapProperties(
        @Name("username") String username,
        @Name("password") String password,
        @Name("name") String name,
        @Name("steam-id") String steamId
) {
}
