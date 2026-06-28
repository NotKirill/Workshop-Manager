package notkirill.workshopmanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "third_party_rate_limits")
public class ThirdPartyRateLimits {
    @Id
    @NotNull
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @NotNull
    @Column(name = "used_requests", nullable = false)
    private int usedRequests;

    @NotNull
    @Column(name = "window_started_at", nullable = false)
    private OffsetDateTime windowStartedAt;

    @NotNull
    @Column(name = "last_request_at", nullable = false)
    private OffsetDateTime lastRequestAt;

    protected ThirdPartyRateLimits() {
        // Required by JPA
    }

    public ThirdPartyRateLimits(String serviceName, int usedRequests, OffsetDateTime windowStartedAt, OffsetDateTime lastRequestAt) {
        this.serviceName = serviceName;
        this.usedRequests = usedRequests;
        this.windowStartedAt = windowStartedAt;
        this.lastRequestAt = lastRequestAt;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getUsedRequests() {
        return usedRequests;
    }

    public void setUsedRequests(int usedRequests) {
        this.usedRequests = usedRequests;
    }

    public OffsetDateTime getWindowStartedAt() {
        return windowStartedAt;
    }

    public void setWindowStartedAt(OffsetDateTime windowStartedAt) {
        this.windowStartedAt = windowStartedAt;
    }

    public OffsetDateTime getLastRequestAt() {
        return lastRequestAt;
    }

    public void setLastRequestAt(OffsetDateTime lastRequestAt) {
        this.lastRequestAt = lastRequestAt;
    }

    @Override
    public String toString() {
        return "ThirdPartyRateLimits{" +
                "serviceName='" + serviceName + '\'' +
                ", usedRequests=" + usedRequests +
                ", windowStartedAt=" + windowStartedAt +
                ", lastRequestAt=" + lastRequestAt +
                '}';
    }
}
