package notkirill.workshopmanager.security.auth.refresh;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import notkirill.workshopmanager.user.ClientInfo;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "uk_refresh_token", columnNames = {"token_hash"})
})
public class RefreshToken {
    @Id
    @NotNull
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @NotNull
    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @NotNull
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Nullable
    @Column(name = "revoked_at", nullable = true)
    private OffsetDateTime revokedAt = null;

    @Nullable
    @Column(name = "replaced_by", nullable = true)
    private UUID replacedBy = null;

    // Long TODO maybe integrate?
    @Embedded
    private ClientInfo clientInfo;

    protected RefreshToken() {
        // Required by JPA
    }

    public RefreshToken(long userId, UUID familyId, String tokenHash, OffsetDateTime expiresAt,
                        OffsetDateTime createdAt, ClientInfo clientInfo) {
        this.userId = userId;
        this.familyId = familyId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.clientInfo = clientInfo;
    }

    public UUID getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public OffsetDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(@Nullable OffsetDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    @Nullable
    public UUID getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(@Nullable UUID replacedBy) {
        this.replacedBy = replacedBy;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", familyId=" + familyId +
                ", tokenHash='" + tokenHash + '\'' +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", revokedAt=" + revokedAt +
                ", replacedBy=" + replacedBy +
                ", clientInfo=" + clientInfo +
                '}';
    }
}
