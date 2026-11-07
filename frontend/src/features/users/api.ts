import {privateApi} from "@/app/axios.ts";
import tokenStorage from "@/app/tokenStorage.ts";
import type {AuthSource, Profile} from "@/features/users/types.ts";

export const profileApi = {
    get: async (): Promise<Profile> => (await privateApi.get<Profile>("/users/me")).data,

    rename: async (name: string): Promise<Profile> => (await privateApi.patch<Profile>("/users/me", {name})).data,

    changePassword: async (currentPassword: string | null, newPassword: string): Promise<void> => {
        await privateApi.post("/users/me/password", {
            current_password: currentPassword,
            new_password: newPassword,
            refresh_token: tokenStorage.getRefreshToken(),
        });
    },

    setIdentity: async (provider: AuthSource, key: string): Promise<Profile> =>
        (await privateApi.put<Profile>(`/users/me/identities/${provider}`, {key})).data,

    removeIdentity: async (provider: AuthSource): Promise<Profile> =>
        (await privateApi.delete<Profile>(`/users/me/identities/${provider}`)).data,

    linkSteam: async (ott: string): Promise<Profile> =>
        (await privateApi.post<Profile>("/users/me/identities/steam", {ott})).data,

    steamLinkUrl: async (): Promise<{ url: string }> =>
        (await privateApi.post<{ url: string }>("/users/me/identities/steam/link-url")).data,
};
