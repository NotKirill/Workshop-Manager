package notkirill.workshopmanager.models.dto;

public record UserGroupResponse(
        Long id,
        Long userId,
        Long gameId,
        String name
) {
}
