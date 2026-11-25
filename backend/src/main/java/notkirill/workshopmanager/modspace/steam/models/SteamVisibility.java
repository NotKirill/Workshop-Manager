package notkirill.workshopmanager.modspace.steam.models;

import notkirill.workshopmanager.item.library.search.SearchField;

public enum SteamVisibility implements SearchField.StoredValue {
    PUBLIC(0),
    FRIENDS_ONLY(1),
    PRIVATE(2),
    UNLISTED(3);

    private final short code;

    SteamVisibility(int code) {
        this.code = (short) code;
    }

    public short code() {
        return code;
    }

    @Override
    public Object storedValue() {
        return code;
    }
}
