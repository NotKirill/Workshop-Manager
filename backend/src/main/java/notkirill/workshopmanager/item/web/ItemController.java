package notkirill.workshopmanager.item.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.item.web.dto.*;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAuthority('ITEMS_READ')")
public class ItemController {
    private final ItemWebService itemWebService;

    @Autowired
    public ItemController(ItemWebService itemWebService) {
        this.itemWebService = itemWebService;
    }

    @PostMapping("/search")
    public ItemPageResponse search(@Valid @RequestBody ItemSearchQuery query,
                                   @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.search(query, principal);
    }

    @GetMapping("/search/help")
    public SearchHelpResponse searchHelp() {
        return itemWebService.searchHelp();
    }

    @GetMapping("/{id}")
    public ItemDetailResponse getItem(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.getItem(id, principal);
    }

    @PostMapping("/register-external")
    @PreAuthorize("hasAuthority('ITEMS_REGISTER')")
    public RegistrationResponse registerExternal(@Valid @RequestBody ExternalItemRegisteringRequest request,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.registerExternal(request, principal);
    }

    @PostMapping("/register-local")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponse registerLocal(@Valid @RequestBody LocalItemRegisteringRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.registerLocal(request, principal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public ItemDetailResponse update(@PathVariable long id, @Valid @RequestBody UpdateItemRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregister(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        itemWebService.unregister(List.of(id), principal);
    }

    @PostMapping("/{id}/refresh")
    @PreAuthorize("hasAuthority('ITEMS_REGISTER')")
    public ItemDetailResponse refresh(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        itemWebService.refresh(List.of(id), principal);
        return itemWebService.getItem(id, principal);
    }

    @PostMapping("/{id}/dependencies/register")
    @PreAuthorize("hasAuthority('ITEMS_REGISTER')")
    public RegistrationResponse registerDependencies(@PathVariable long id,
                                                     @RequestBody(required = false) RegisterDependenciesRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.registerDependencies(id, request, principal);
    }

    @PostMapping("/{id}/dependencies/review")
    @PreAuthorize("hasAuthority('ITEMS_REGISTER')")
    public RegistrationResponse reviewDependencies(@PathVariable long id,
                                                   @Valid @RequestBody ReviewDependenciesRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.reviewDependencies(id, request, principal);
    }

    @PostMapping("/bulk/unregister")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public BulkResultResponse bulkUnregister(@Valid @RequestBody ItemIdsRequest request,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.unregister(request.itemIds(), principal);
    }

    @PostMapping("/bulk/refresh")
    @PreAuthorize("hasAuthority('ITEMS_REGISTER')")
    public BulkResultResponse bulkRefresh(@Valid @RequestBody ItemIdsRequest request,
                                          @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.refresh(request.itemIds(), principal);
    }

    @PostMapping("/bulk/auto-backup")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public BulkResultResponse bulkAutoBackup(@Valid @RequestBody BulkAutoBackupRequest request,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        return itemWebService.setAutoBackup(request, principal);
    }
}
