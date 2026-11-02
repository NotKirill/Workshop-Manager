package notkirill.workshopmanager.item.organization.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.item.organization.group.GroupService;
import notkirill.workshopmanager.item.organization.group.UserGroup;
import notkirill.workshopmanager.item.web.dto.BulkResultResponse;
import notkirill.workshopmanager.item.web.dto.ItemIdsRequest;
import notkirill.workshopmanager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasAuthority('ITEMS_READ')")
public class GroupController {
    private final GroupService groupService;
    private final CollectionMapper collectionMapper;

    @Autowired
    public GroupController(GroupService groupService, CollectionMapper collectionMapper) {
        this.groupService = groupService;
        this.collectionMapper = collectionMapper;
    }

    @GetMapping
    public List<CollectionResponse> list(@RequestParam(name = "game_id", required = false) Long gameId,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return groupService.list(principal.getUserId(), gameId).stream().map(collectionMapper::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionResponse create(@Valid @RequestBody CollectionRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        UserGroup created = groupService.create(principal.getUserId(), request.name(), request.gameId());
        return collectionMapper.toResponse(created, 0);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public CollectionResponse rename(@PathVariable long id, @Valid @RequestBody RenameRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        return collectionMapper.toResponse(groupService.rename(principal.getUserId(), id, request.name()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        groupService.delete(principal.getUserId(), id);
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public BulkResultResponse addItems(@PathVariable long id, @Valid @RequestBody ItemIdsRequest request,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        return new BulkResultResponse(groupService.addItems(principal.getUserId(), id, request.itemIds()));
    }

    @PostMapping("/{id}/items/remove")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public BulkResultResponse removeItems(@PathVariable long id, @Valid @RequestBody ItemIdsRequest request,
                                          @AuthenticationPrincipal UserPrincipal principal) {
        return new BulkResultResponse(groupService.removeItems(principal.getUserId(), id, request.itemIds()));
    }
}
