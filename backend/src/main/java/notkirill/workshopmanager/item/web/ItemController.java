package notkirill.workshopmanager.item.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.item.web.dto.*;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
