package notkirill.workshopmanager.security.auth;

import com.fasterxml.jackson.annotation.JsonTypeName;
import notkirill.workshopmanager.ott.TokenPayload;

@JsonTypeName("APPROVED_AUTH")
public record ApprovedAuthPayload(AuthSource provider, String providerKey) implements TokenPayload {
    @Override
    public String getType() {
        return "APPROVED_AUTH";
    }
}