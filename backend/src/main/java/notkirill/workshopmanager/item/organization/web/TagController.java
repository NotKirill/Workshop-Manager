package notkirill.workshopmanager.item.organization.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.item.organization.tag.UserTag;
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
@RequestMapping("/api/tags")
@PreAuthorize("hasAuthority('ITEMS_READ')")
public class TagController {
    private final TagService tagService;
    private final CollectionMapper collectionMapper;

    @Autowired
    public TagController(TagService tagService, CollectionMapper collectionMapper) {
        this.tagService = tagService;
        this.collectionMapper = collectionMapper;
    }

    @GetMapping
    public List<CollectionResponse> list(@RequestParam(name = "game_id", required = false) Long gameId,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        return tagService.list(principal.getUserId(), gameId).stream().map(collectionMapper::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    public CollectionResponse create(@Valid @RequestBody CollectionRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        UserTag created = tagService.create(principal.getUserId(), request.name(), request.gameId());
        return collectionMapper.toResponse(created, 0);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public CollectionResponse rename(@PathVariable long id, @Valid @RequestBody RenameRequest request,
                                     @AuthenticationPrincipal UserPrincipal principal) {
        return collectionMapper.toResponse(tagService.rename(principal.getUserId(), id, request.name()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        tagService.delete(principal.getUserId(), id);
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public BulkResultResponse addItems(@PathVariable long id, @Valid @RequestBody ItemIdsRequest request,
                                       @AuthenticationPrincipal UserPrincipal principal) {
        return new BulkResultResponse(tagService.addToItems(principal.getUserId(), id, request.itemIds()));
    }

    @PostMapping("/{id}/items/remove")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public BulkResultResponse removeItems(@PathVariable long id, @Valid @RequestBody ItemIdsRequest request,
                                          @AuthenticationPrincipal UserPrincipal principal) {
        return new BulkResultResponse(tagService.removeFromItems(principal.getUserId(), id, request.itemIds()));
    }
}
