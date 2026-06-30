package notkirill.workshopmanager.models.dto;

public record UserSharedPermissionResponse(
        Long userProviderId,
        Long userConsumerId,
        Short permissionId
) {
}
