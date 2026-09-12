package notkirill.workshopmanager.user.bootstrap;

import notkirill.workshopmanager.common.Texts;
import notkirill.workshopmanager.security.auth.AuthSource;
import notkirill.workshopmanager.user.UserAccountService;
import notkirill.workshopmanager.user.UserService;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final AdminBootstrapProperties properties;
    private final UserService userService;
    private final UserAccountService accountService;

    @Autowired
    public AdminBootstrapRunner(AdminBootstrapProperties properties, UserService userService,
                                UserAccountService accountService) {
        this.properties = properties;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (userService.count() > 0) {
            return;
        }
        boolean hasLocal = Texts.blankToNull(properties.username()) != null
                && Texts.blankToNull(properties.password()) != null;
        boolean hasSteam = Texts.blankToNull(properties.steamId()) != null;
        if (!hasLocal && !hasSteam) {
            log.warn("No users exist and no bootstrap admin is configured "
                             + "(application.bootstrap.admin.username/password or steam-id); nobody can sign in");
            return;
        }
        Map<AuthSource, String> identities = new EnumMap<>(AuthSource.class);
        if (hasLocal) {
            identities.put(AuthSource.LOCAL_USERNAME, properties.username());
        }
        if (hasSteam) {
            identities.put(AuthSource.STEAM, properties.steamId());
        }
        accountService.create(
                properties.name() != null ? properties.name() : "Administrator",
                hasLocal ? properties.password() : null,
                identities,
                EnumSet.allOf(PermissionEnum.class)
        );
        log.info("Bootstrap administrator account created");
    }
}
