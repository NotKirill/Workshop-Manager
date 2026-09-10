package notkirill.workshopmanager.user.permission;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Long TODO implement somewhere a shared permission between users
public enum PermissionEnum {
    // Basic permissions of a regular user
    ITEMS_READ(
            "Read library",
            "See the own library: items, tags, groups, games, backups and media"
    ),
    ITEMS_WRITE(
            "Edit library",
            "Edit, organize and remove items of the own library, register local items"
    ),
    ITEMS_REGISTER(
            "Register external items",
            "Register and refresh items from modspaces (uses the shared Steam API quota)"
    ),
    CONTENT_IMPORT(
            "Store content",
            "Request backups, upload files, use storing modes with content (uses shared storage)"
    ),
    MEDIA_UPLOAD(
            "Upload media",
            "Upload images to items (uses shared storage)"
    ),

    // Administration
    USERS_MANAGE(
            "Manage users",
            "Create, edit, disable and delete user accounts and their sign-in identities"
    ),
    GAMES_MANAGE(
            "Manage games",
            "Rename games and manage game compatibility"
    ),
    CONTENT_MANAGE(
            "Manage content",
            "Manage content backups of any item (delete backups shared with other users)"
    ),
    MAINTENANCE_MANAGE(
            "Maintenance",
            "Inspect and retry background jobs (cleanup, sync, content jobs)"
    );

    public static final Set<PermissionEnum> DEFAULT_USER = Collections.unmodifiableSet(
            EnumSet.of(ITEMS_READ, ITEMS_WRITE, ITEMS_REGISTER, CONTENT_IMPORT, MEDIA_UPLOAD));

    private final String label;
    private final String description;

    PermissionEnum(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
