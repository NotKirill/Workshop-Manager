export const steamOpenIdService = {
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