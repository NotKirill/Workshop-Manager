package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.exception.ForbiddenException;
import notkirill.workshopmanager.item.registration.ItemRegistrationService;
import notkirill.workshopmanager.item.registration.RegistrationResult;
import notkirill.workshopmanager.item.web.dto.*;
import notkirill.workshopmanager.security.PermissionAuthorities;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemWebService {
    private final ItemQueryService queryService;
    private final ItemDetailService detailService;
    private final ItemRegistrationService registrationService;

    @Autowired
    public ItemWebService(ItemQueryService queryService, ItemDetailService detailService,
                          ItemRegistrationService registrationService) {
        this.queryService = queryService;
        this.detailService = detailService;
        this.registrationService = registrationService;
    }

    private static boolean canManageContent(UserPrincipal principal) {
        return PermissionAuthorities.has(principal, PermissionEnum.CONTENT_MANAGE);
    }

    private static void requireAutoBackupAllowed(boolean autoBackup, UserPrincipal principal) {
        if (autoBackup && !PermissionAuthorities.has(principal, PermissionEnum.CONTENT_IMPORT)) {
            throw new ForbiddenException(
                    "CONTENT_IMPORT_FORBIDDEN",
                    "Storing content needs the content import permission"
            );
        }
    }

    public ItemPageResponse search(ItemSearchQuery query, UserPrincipal principal) {
        return queryService.search(principal.getUserId(), query);
    }

    public SearchHelpResponse searchHelp() {
        return queryService.getSearchHelp();
    }

    public ItemDetailResponse getItem(long id, UserPrincipal principal) {
        return detailService.getDetail(principal.getUserId(), id, canManageContent(principal));
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
