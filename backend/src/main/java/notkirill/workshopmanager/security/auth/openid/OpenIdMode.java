package notkirill.workshopmanager.security.auth.openid;

public enum OpenIdMode {
    CHECKID_IMMEDIATE("checkid_immediate"),
    CHECKID_SETUP("checkid_setup"),
    ID_RES("id_res"),
    CANCEL("cancel"),
    CHECK_AUTHENTICATION("check_authentication"),
    SETUP_NEEDED("setup_needed"),
    ;

    private final String value;

    OpenIdMode(String mode) {
        value = mode;
    }

    public String getValue() {
        return value;
    }
}
