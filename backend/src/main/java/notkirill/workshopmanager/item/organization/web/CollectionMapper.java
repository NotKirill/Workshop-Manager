package notkirill.workshopmanager.item.organization.web;

import notkirill.workshopmanager.item.organization.group.GroupService;
import notkirill.workshopmanager.item.organization.group.UserGroup;
import notkirill.workshopmanager.item.organization.tag.TagService;
import notkirill.workshopmanager.item.organization.tag.UserTag;
import notkirill.workshopmanager.item.web.dto.NamedRef;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class CollectionMapper {
    private static final Comparator<NamedRef> BY_NAME = Comparator.comparing(
            NamedRef::name,
            String.CASE_INSENSITIVE_ORDER
    );

    public CollectionResponse toResponse(TagService.CountedTag counted) {
        return toResponse(counted.tag(), counted.itemCount());
    }

    public CollectionResponse toResponse(UserTag tag, long itemCount) {
        return new CollectionResponse(tag.getId(), tag.getDisplayName(), tag.getGameId(), itemCount);
    }

    public CollectionResponse toResponse(GroupService.CountedGroup counted) {
        return toResponse(counted.group(), counted.itemCount());
    }

    public CollectionResponse toResponse(UserGroup group, long itemCount) {
        return new CollectionResponse(group.getId(), group.getName(), group.getGameId(), itemCount);
    }

    public List<NamedRef> tagRefs(Collection<UserTag> tags) {
        return tags.stream().map(tag -> new NamedRef(tag.getId(), tag.getDisplayName())).sorted(BY_NAME).toList();
    }

    public List<NamedRef> groupRefs(Collection<UserGroup> groups) {
        return groups.stream().map(group -> new NamedRef(group.getId(), group.getName())).sorted(BY_NAME).toList();
    }
}
