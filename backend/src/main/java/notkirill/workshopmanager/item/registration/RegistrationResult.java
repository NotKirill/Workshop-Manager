package notkirill.workshopmanager.item.registration;

import notkirill.workshopmanager.modspace.ModspaceType;

public record RegistrationResult(
        ModspaceType modspace,
        String originalId,
        Status status,
        Long personalId,
        Long internalId,
        String title,
        String reason,
        Long dependencyOf
) {
    public enum Status {
        REGISTERED, ALREADY_REGISTERED, INVALID, FAILED
    }
}
