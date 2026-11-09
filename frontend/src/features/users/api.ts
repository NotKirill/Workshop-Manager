import {privateApi} from "@/app/axios.ts";
import tokenStorage from "@/app/tokenStorage.ts";
import type {AdminUser, AuthSource, CreateUserRequest, PermissionInfo, Profile, UpdateUserRequest} from "@/features/users/types.ts";

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

export const adminUsersApi = {
    list: async (): Promise<AdminUser[]> => (await privateApi.get<AdminUser[]>("/admin/users")).data,

    permissions: async (): Promise<PermissionInfo[]> =>
        (await privateApi.get<PermissionInfo[]>("/admin/users/permissions")).data,

    create: async (request: CreateUserRequest): Promise<AdminUser> =>
        (await privateApi.post<AdminUser>("/admin/users", request)).data,

    update: async (id: number, request: UpdateUserRequest): Promise<AdminUser> =>
        (await privateApi.patch<AdminUser>(`/admin/users/${id}`, request)).data,

    resetPassword: async (id: number, password: string): Promise<void> => {
        await privateApi.post(`/admin/users/${id}/password`, {password});
    },

    setIdentity: async (id: number, provider: AuthSource, key: string): Promise<AdminUser> =>
        (await privateApi.put<AdminUser>(`/admin/users/${id}/identities/${provider}`, {key})).data,

    removeIdentity: async (id: number, provider: AuthSource): Promise<AdminUser> =>
        (await privateApi.delete<AdminUser>(`/admin/users/${id}/identities/${provider}`)).data,

    remove: async (id: number): Promise<void> => {
        await privateApi.delete(`/admin/users/${id}`);
    },
};
