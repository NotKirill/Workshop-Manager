package notkirill.workshopmanager.item.web;

import jakarta.validation.Valid;
import notkirill.workshopmanager.item.enums.RelationType;
import notkirill.workshopmanager.item.organization.group.GroupService;
import notkirill.workshopmanager.item.organization.rating.RatingService;
import notkirill.workshopmanager.item.organization.relation.RelationService;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.item.organization.web.CollectionMapper;
import notkirill.workshopmanager.item.web.dto.IdSetRequest;
import notkirill.workshopmanager.item.web.dto.NamedRef;
import notkirill.workshopmanager.item.web.dto.RatingRequest;
import notkirill.workshopmanager.item.web.dto.RelationRequest;
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
public class ItemOrganizationController {
    private final RatingService ratingService;
    private final TagService tagService;
    private final GroupService groupService;
    private final RelationService relationService;
    private final CollectionMapper collectionMapper;

    @Autowired
    public ItemOrganizationController(RatingService ratingService, TagService tagService, GroupService groupService,
                                      RelationService relationService, CollectionMapper collectionMapper) {
        this.ratingService = ratingService;
        this.tagService = tagService;
        this.groupService = groupService;
        this.relationService = relationService;
        this.collectionMapper = collectionMapper;
    }

    @PutMapping("/{id}/rating")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rate(@PathVariable long id, @Valid @RequestBody RatingRequest request,
                     @AuthenticationPrincipal UserPrincipal principal) {
        ratingService.rate(principal.getUserId(), id, request.score());
    }

    @DeleteMapping("/{id}/rating")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearRating(@PathVariable long id, @AuthenticationPrincipal UserPrincipal principal) {
        ratingService.clear(principal.getUserId(), id);
    }

    @PutMapping("/{id}/tags")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public List<NamedRef> setTags(@PathVariable long id, @Valid @RequestBody IdSetRequest request,
                                  @AuthenticationPrincipal UserPrincipal principal) {
        return collectionMapper.tagRefs(tagService.setItemTags(principal.getUserId(), id, request.ids()));
    }

    @PutMapping("/{id}/groups")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    public List<NamedRef> setGroups(@PathVariable long id, @Valid @RequestBody IdSetRequest request,
                                    @AuthenticationPrincipal UserPrincipal principal) {
        return collectionMapper.groupRefs(groupService.setItemGroups(principal.getUserId(), id, request.ids()));
    }

    @PostMapping("/{id}/relations")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addRelation(@PathVariable long id, @Valid @RequestBody RelationRequest request,
                            @AuthenticationPrincipal UserPrincipal principal) {
        relationService.add(principal.getUserId(), id, request.targetId(), request.relation());
    }

    @DeleteMapping("/{id}/relations/{targetId}/{relation}")
    @PreAuthorize("hasAuthority('ITEMS_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRelation(@PathVariable long id, @PathVariable long targetId,
                               @PathVariable RelationType relation,
                               @AuthenticationPrincipal UserPrincipal principal) {
        relationService.remove(principal.getUserId(), id, targetId, relation);
    }
}
