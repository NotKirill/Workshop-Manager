package notkirill.workshopmanager.security.auth;

public enum AuthSource {
    LOCAL_USERNAME(true, "username"),
    LOCAL_EMAIL(true, "email"),
    STEAM(false, "Steam account");

    private final boolean passwordBased;
    private final String label;

    AuthSource(boolean passwordBased, String label) {
        this.passwordBased = passwordBased;
        this.label = label;
    }

    public static AuthSource ofLogin(String login) {
        return login.contains("@") ? LOCAL_EMAIL : LOCAL_USERNAME;
    }

    public boolean isPasswordBased() {
        return passwordBased;
    }

    public String getLabel() {
        return label;
    }
}
