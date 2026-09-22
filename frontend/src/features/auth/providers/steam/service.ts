const LOGIN_STATE_KEY = "steam_login_state";

export const steamOpenIdService = {
    createLoginState: (): string => {
        const bytes = crypto.getRandomValues(new Uint8Array(24));
        const state = Array.from(bytes, b => b.toString(16).padStart(2, "0")).join("");
        sessionStorage.setItem(LOGIN_STATE_KEY, state);
        return state;
    },

    consumeLoginState: (state: string | null): boolean => {
        const expected = sessionStorage.getItem(LOGIN_STATE_KEY);
        sessionStorage.removeItem(LOGIN_STATE_KEY);
        return expected !== null && state === expected;
    },

    redirectToSteamLogin: (url: string) => {
        const isValidSteamUrl = (urlToCheck: string) => {
            try {
                const url = new URL(urlToCheck);
                return url.origin === 'https://steamcommunity.com' && url.pathname.startsWith('/openid');
            } catch {
                return false;
            }
        }

        if (!isValidSteamUrl(url)) {
            throw new Error("Invalid Steam OpenID URL provided");
        }

        globalThis.location.href = url;
    },
};
export default steamOpenIdService;