package notkirill.workshopmanager.ratelimit;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.bind.Name;

import java.time.Duration;

public record QuotaProfile(
        @Name("max-requests") @NotNull @Positive Integer maxRequests,
        @Name("window") @NotNull Duration window,
        @Name("background-share") @DecimalMin("0.0") @DecimalMax("1.0") Double backgroundShare
) {
    public static QuotaProfile.Builder builder() {
        return new QuotaProfile.Builder();
    }

    public QuotaProfile.Builder toBuilder() {
        return builder().maxRequests(maxRequests).window(window).backgroundShare(backgroundShare);
    }

    public long reservedForInteractive() {
        long background = (long) Math.floor(maxRequests * backgroundShare + 1e-9);
        return maxRequests - background;
    }

    public static final class Builder {
        private Integer maxRequests;
        private Duration window;
        private Double backgroundShare = 0.7;

        private Builder() {
        }

        public QuotaProfile.Builder maxRequests(Integer maxRequests) {
            if (maxRequests != null) {
                this.maxRequests = maxRequests;
            }
            return this;
        }

        public QuotaProfile.Builder window(Duration window) {
            if (window != null) {
                this.window = window;
            }
            return this;
        }

        public QuotaProfile.Builder backgroundShare(Double backgroundShare) {
            if (backgroundShare != null) {
                this.backgroundShare = backgroundShare;
            }
            return this;
        }

        public QuotaProfile.Builder override(QuotaProfile configured) {
            if (configured == null) {
                return this;
            }
            return maxRequests(configured.maxRequests())
                    .window(configured.window())
                    .backgroundShare(configured.backgroundShare());
        }

        public QuotaProfile build() {
            return new QuotaProfile(maxRequests, window, backgroundShare);
        }
    }

}
