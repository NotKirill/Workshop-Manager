package notkirill.workshopmanager.item.web;

import notkirill.workshopmanager.exception.ForbiddenException;
import notkirill.workshopmanager.item.library.ItemCommandService;
import notkirill.workshopmanager.item.personal.PersonalItemService;
import notkirill.workshopmanager.item.registration.ItemRegistrationService;
import notkirill.workshopmanager.item.registration.RegistrationResult;
import notkirill.workshopmanager.item.web.dto.*;
import notkirill.workshopmanager.security.PermissionAuthorities;
import notkirill.workshopmanager.security.UserPrincipal;
import notkirill.workshopmanager.user.permission.PermissionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ItemWebService {
    private final ItemQueryService queryService;
    private final ItemCommandService commandService;
    private final ItemDetailService detailService;
    private final ItemRegistrationService registrationService;
    private final PersonalItemService personalService;

    @Autowired
    public ItemWebService(ItemQueryService queryService, ItemCommandService commandService,
                          ItemDetailService detailService, ItemRegistrationService registrationService,
                          PersonalItemService personalService) {
        this.queryService = queryService;
        this.commandService = commandService;
        this.detailService = detailService;
        this.registrationService = registrationService;
        this.personalService = personalService;
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

    public RegistrationResponse registerDependencies(long id, RegisterDependenciesRequest request,
                                                     UserPrincipal principal) {
        var personal = personalService.requireOwned(principal.getUserId(), id);
        boolean autoBackup = request != null && request.autoBackup() != null ? request.autoBackup()
                : personal.isAutoBackup();
        requireAutoBackupAllowed(autoBackup, principal);
        return toResponse(registrationService.registerDependencies(
                principal.getUserId(), id, autoBackup,
                new HashSet<>()
        ));
    }

    public RegistrationResponse reviewDependencies(long id, ReviewDependenciesRequest request,
                                                   UserPrincipal principal) {
        requireAutoBackupAllowed(personalService.requireOwned(principal.getUserId(), id).isAutoBackup(), principal);
        return toResponse(registrationService.reviewDependencies(
                principal.getUserId(), id, request.add(),
                request.remove()
        ));
    }

    public ItemDetailResponse update(long id, UpdateItemRequest request, UserPrincipal principal) {
        if (personalService.requireOwned(principal.getUserId(), id).isAutoBackup() != request.autoBackup()) {
            requireAutoBackupAllowed(request.autoBackup(), principal);
        }
        commandService.update(principal.getUserId(), id, request);
        return getItem(id, principal);
    }

    public BulkResultResponse unregister(List<Long> ids, UserPrincipal principal) {
        return new BulkResultResponse(commandService.unregister(principal.getUserId(), ids));
    }

    public BulkResultResponse refresh(List<Long> ids, UserPrincipal principal) {
        return new BulkResultResponse(commandService.refresh(principal.getUserId(), ids));
    }

    public BulkResultResponse setAutoBackup(BulkAutoBackupRequest request, UserPrincipal principal) {
        requireAutoBackupAllowed(request.autoBackup(), principal);
        return new BulkResultResponse(commandService.setAutoBackup(
                principal.getUserId(), request.itemIds(),
                request.autoBackup()
        ));
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
