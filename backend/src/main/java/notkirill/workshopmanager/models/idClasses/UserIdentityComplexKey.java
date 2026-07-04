package notkirill.workshopmanager.models.idClasses;

import notkirill.workshopmanager.models.enums.AuthProvider;

import java.util.Objects;

// TODO rename
public class UserIdentityComplexKey {
    private long userId;
    private AuthProvider provider;

    protected UserIdentityComplexKey() {
        // Required by JPA
    }

    public UserIdentityComplexKey(long userId, AuthProvider provider) {
        this.userId = userId;
        this.provider = provider;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserIdentityComplexKey that)) return false;
        return userId == that.userId && provider == that.provider;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, provider);
    }

    @Override
    public String toString() {
        return "UserIdentityComplexKey{" +
                "userId=" + userId +
                ", provider=" + provider +
                '}';
    }
}
