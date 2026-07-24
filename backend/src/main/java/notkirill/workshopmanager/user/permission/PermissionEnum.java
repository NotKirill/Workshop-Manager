package notkirill.workshopmanager.user.permission;

import java.util.HashMap;
import java.util.Map;

// Long TODO integrate and populate
// Long TODO implement somewhere a shared permission between users
public enum PermissionEnum {
    TEST_EXAMPLE("resource:action");

    private static final Map<String, PermissionEnum> BY_NAME = new HashMap<>();

    static {
        for (PermissionEnum permission : PermissionEnum.values()) {
            BY_NAME.put(permission.trueName, permission);
        }
    }

    private final String trueName;

    PermissionEnum(String trueName) {
        this.trueName = trueName;
    }

    public static PermissionEnum getByTrueName(String trueName) {
        PermissionEnum result = BY_NAME.get(trueName);
        if (result == null) {
            throw new IllegalArgumentException("Invalid permission: " + trueName);
        }
        return result;
    }

    public String getTrueName() {
        return trueName;
    }
}
