package notkirill.workshopmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.models.enums.AuthProvider;
import notkirill.workshopmanager.models.idClasses.UserIdentityComplexKey;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "user_identities",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})
        })
@IdClass(UserIdentityComplexKey.class)
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
}
