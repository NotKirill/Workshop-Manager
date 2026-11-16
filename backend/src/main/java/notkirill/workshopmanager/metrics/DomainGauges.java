package notkirill.workshopmanager.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import notkirill.workshopmanager.author.refresh.AuthorRefreshService;
import notkirill.workshopmanager.content.ContentJobService;
import notkirill.workshopmanager.content.ContentUploadService;
import notkirill.workshopmanager.content.enums.ContentJobStatus;
import notkirill.workshopmanager.game.lookup.GameLookupService;
import notkirill.workshopmanager.item.enums.ItemLifecycleState;
import notkirill.workshopmanager.item.shared.ItemInternalService;
import notkirill.workshopmanager.media.source.MediaSourceSyncService;
import notkirill.workshopmanager.ratelimit.ThirdPartyApi;
import notkirill.workshopmanager.ratelimit.ThirdPartyRateLimitService;
import notkirill.workshopmanager.user.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DomainGauges implements MeterBinder {
    private static final Set<ContentJobStatus> JOB_STATUSES =
            Set.of(ContentJobStatus.PENDING, ContentJobStatus.RUNNING, ContentJobStatus.FAILED);

    private final ItemInternalService internalService;
    private final ContentJobService contentJobService;
    private final ContentUploadService contentUploadService;
    private final UserService userService;
    private final AuthorRefreshService authorRefreshService;
    private final GameLookupService gameLookupService;
    private final MediaSourceSyncService mediaSourceSyncService;
    private final ThirdPartyRateLimitService rateLimitService;
    private final List<ThirdPartyApi> apis;

    @Autowired
    public DomainGauges(ItemInternalService internalService, ContentJobService contentJobService,
                        ContentUploadService contentUploadService, UserService userService,
                        AuthorRefreshService authorRefreshService, GameLookupService gameLookupService,
                        MediaSourceSyncService mediaSourceSyncService, ThirdPartyRateLimitService rateLimitService,
                        List<ThirdPartyApi> apis) {
        this.internalService = internalService;
        this.contentJobService = contentJobService;
        this.contentUploadService = contentUploadService;
        this.userService = userService;
        this.authorRefreshService = authorRefreshService;
        this.gameLookupService = gameLookupService;
        this.mediaSourceSyncService = mediaSourceSyncService;
        this.rateLimitService = rateLimitService;
        this.apis = apis;
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        for (ItemLifecycleState state : ItemLifecycleState.values()) {
            Gauge.builder("workshop.items", () -> internalService.countByLifecycleState(state))
                    .description("Items (shared identities) per lifecycle state")
                    .tag("state", state.name())
                    .register(registry);
        }
        for (ContentJobStatus status : JOB_STATUSES) {
            Gauge.builder("workshop.content.jobs.current", () -> contentJobService.countByStatus(status))
                    .description("Content jobs waiting, running or failed")
                    .tag("status", status.name())
                    .register(registry);
        }
        Gauge.builder("workshop.content.uploads.open", contentUploadService::countOpen)
                .description("Backup uploads in progress")
                .register(registry);
        Gauge.builder("workshop.users", userService::count)
                .description("User accounts")
                .register(registry);
        Gauge.builder("workshop.authors.queued", authorRefreshService::countQueued)
                .description("Authors waiting for a refresh from their modspace")
                .register(registry);
        Gauge.builder("workshop.games.queued", gameLookupService::countQueued)
                .description("Games whose name is still being looked up")
                .register(registry);
        Gauge.builder("workshop.media.syncs.given_up", mediaSourceSyncService::countGivenUp)
                .description("Copies of modspace media that failed too often")
                .register(registry);
        for (ThirdPartyApi api : apis) {
            bindQuota(registry, api);
        }
    }

    private void bindQuota(MeterRegistry registry, ThirdPartyApi api) {
        Gauge.builder(
                        "workshop.ratelimit.tokens", () -> {
                            Long available = rateLimitService.getBucketStatus(api).availableTokens();
                            return available == null ? Double.NaN : available;
                        }
                )
                .description("Calls left in the current quota window of an external API")
                .tag("api", api.getBeanName())
                .register(registry);
        Gauge.builder("workshop.ratelimit.capacity", () -> api.getProfile().quotaProfile().maxRequests())
                .description("Calls allowed per quota window")
                .tag("api", api.getBeanName())
                .register(registry);
        Gauge.builder("workshop.ratelimit.reserved", () -> api.getProfile().quotaProfile().reservedForInteractive())
                .description("Calls scheduled jobs leave for users and admins")
                .tag("api", api.getBeanName())
                .register(registry);
    }
}
