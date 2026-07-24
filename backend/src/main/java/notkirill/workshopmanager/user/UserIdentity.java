package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.AuthProvider;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

@Entity
@Table(name = "user_identities",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})
        })
@IdClass(UserIdentity.CompositeId.class)
public class UserIdentity {
    @Id
    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Id
    @NotNull
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "provider", columnDefinition = "auth_provider", nullable = false)
    private AuthProvider provider;

    @NotNull
    @Column(name = "provider_key", nullable = false)
    private String providerKey;

    protected UserIdentity() {
        // Required by JPA
    }

    public UserIdentity(long userId, AuthProvider provider, String providerKey) {
        this.userId = userId;
        this.provider = provider;
        this.providerKey = providerKey;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderKey() {
        return providerKey;
    }

    public void setProviderKey(String providerKey) {
        this.providerKey = providerKey;
    }

    @Override
    public String toString() {
        return "UserIdentity{" +
                "userId=" + userId +
                ", provider=" + provider +
                ", providerKey='" + providerKey + '\'' +
                '}';
    }

    public static class CompositeId {
        private long userId;
        private AuthSource provider;

        protected CompositeId() {
            // Required by JPA
        }

        public CompositeId(long userId, AuthSource provider) {
            this.userId = userId;
            this.provider = provider;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public AuthSource getProvider() {
            return provider;
        }

        public void setProvider(AuthSource provider) {
            this.provider = provider;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, provider);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompositeId that)) return false;
            return userId == that.userId && provider == that.provider;
        }

        @Override
        public String toString() {
            return "UserIdentity.CompositeId{" +
                    "userId=" + userId +
                    ", provider=" + provider +
                    '}';
        }
    }
}