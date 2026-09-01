package notkirill.workshopmanager.security.auth.openid;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "open_id_nonces")
@IdClass(OpenIdNonce.CompositeId.class)
@EntityListeners(AuditingEntityListener.class)
public class OpenIdNonce {

    @Id
    @NotNull
    @Column(name = "value", nullable = false)
    private String value;

    @Id
    @NotNull
    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @NotNull
    @Column(name = "registered_at", nullable = false)
    @CreatedDate
    private OffsetDateTime registeredAt;

    protected OpenIdNonce() {
        // Required by JPA
    }

    public OpenIdNonce(String value, String endpoint) {
        this.value = value;
        this.endpoint = endpoint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    @Override
    public String toString() {
        return "OpenIdNonce{" +
                "value='" + value + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }

    public static class CompositeId implements Serializable {
        private String value;
        private String endpoint;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(String value, String endpoint) {
            this.value = value;
            this.endpoint = endpoint;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, endpoint);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return Objects.equals(value, that.value) && Objects.equals(endpoint, that.endpoint);
        }

        @Override
        public String toString() {
            return "CompositeId{" +
                    "value='" + value + '\'' +
                    ", endpoint='" + endpoint + '\'' +
                    '}';
        }
    }
}
