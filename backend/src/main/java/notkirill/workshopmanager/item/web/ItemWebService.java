package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.exception.ForbiddenException;
import notkirill.workshopmanager.item.registration.ItemRegistrationService;
import notkirill.workshopmanager.item.registration.RegistrationResult;
import notkirill.workshopmanager.item.web.dto.ExternalItemRegisteringRequest;
import notkirill.workshopmanager.item.web.dto.LocalItemRegisteringRequest;
import notkirill.workshopmanager.item.web.dto.RegistrationResponse;
import notkirill.workshopmanager.security.PermissionAuthorities;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemWebService {
    private final ItemRegistrationService registrationService;

    @Autowired
    public ItemWebService(ItemRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    private static void requireAutoBackupAllowed(boolean autoBackup, UserPrincipal principal) {
        if (autoBackup && !PermissionAuthorities.has(principal, PermissionEnum.CONTENT_IMPORT)) {
            throw new ForbiddenException(
                    "CONTENT_IMPORT_FORBIDDEN",
                    "Storing content needs the content import permission"
            );
        }
    }

    public RegistrationResponse registerExternal(ExternalItemRegisteringRequest request, UserPrincipal principal) {
        requireAutoBackupAllowed(request.autoBackup(), principal);
        return toResponse(registrationService.registerExternal(principal.getUserId(), request));
    }

    public RegistrationResponse registerLocal(LocalItemRegisteringRequest request, UserPrincipal principal) {
        return toResponse(List.of(registrationService.registerLocal(principal.getUserId(), request)));
    }

    private RegistrationResponse toResponse(List<RegistrationResult> results) {
        return new RegistrationResponse(results.stream()
                                                .map(result -> new RegistrationResponse.Result(
                                                        result.modspace(), result.originalId(), result.status(),
                                                        result.personalId(), result.internalId(), result.title(),
                                                        result.reason(),
                                                        result.dependencyOf()
                                                ))
                                                .toList());
    }
}
