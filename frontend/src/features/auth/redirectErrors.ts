const REDIRECT_ERRORS: Record<string, string> = {
    steam_cancelled: "Steam sign-in was cancelled.",
    steam_unavailable: "Steam sign-in is unavailable right now. Please try again in a minute.",
    steam_failed: "Steam sign-in failed. Please try again.",
    not_linked: "This Steam account is not linked to any user. Ask an administrator for access, or sign in and link it in your profile.",
    disabled: "This account is disabled.",
    missing_token: "Sign-in link is incomplete. Please try again.",
    foreign_flow: "This sign-in was not started in this browser. Please start it again.",
};

export const redirectErrorMessage = (key: string, fallback: string): string => REDIRECT_ERRORS[key] ?? fallback;
