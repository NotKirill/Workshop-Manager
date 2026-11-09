import {useMutation} from "@tanstack/react-query";
import queryClient from "@/app/queryClient.ts";
import {meQueryKey} from "@/features/auth/queries.ts";
import steamOpenIdService from "@/features/auth/providers/steam/service.ts";
import {adminUsersApi, profileApi} from "@/features/users/api.ts";
import {adminUsersQueryKey} from "@/features/users/queries.ts";
import type {AuthSource, CreateUserRequest, Profile, UpdateUserRequest} from "@/features/users/types.ts";
import {notifyError} from "@/lib/notify.ts";

const showProfile = (profile: Profile) => void queryClient.setQueryData(meQueryKey, profile);

const invalidateAdminUsers = () => void queryClient.invalidateQueries({queryKey: adminUsersQueryKey});

// Own account

export const useRenameProfile = () =>
    useMutation({
        mutationFn: (name: string) => profileApi.rename(name),
        onSuccess: showProfile,
        onError: error => notifyError(error),
    });

export const useChangePassword = () =>
    useMutation({
        mutationFn: ({currentPassword, newPassword}: { currentPassword: string | null; newPassword: string }) =>
            profileApi.changePassword(currentPassword, newPassword),
        onSuccess: () => void queryClient.invalidateQueries({queryKey: meQueryKey}),
        onError: error => notifyError(error),
    });

export const useSetIdentity = () =>
    useMutation({
        mutationFn: ({provider, key}: { provider: AuthSource; key: string }) => profileApi.setIdentity(provider, key),
        onSuccess: showProfile,
        onError: error => notifyError(error),
    });

export const useRemoveIdentity = () =>
    useMutation({
        mutationFn: (provider: AuthSource) => profileApi.removeIdentity(provider),
        onSuccess: showProfile,
        onError: error => notifyError(error),
    });

export const useStartSteamLink = () =>
    useMutation({
        mutationFn: profileApi.steamLinkUrl,
        onSuccess: data => steamOpenIdService.redirectToSteamLogin(data.url),
        onError: error => notifyError(error),
    });

export const useFinishSteamLink = () =>
    useMutation({
        mutationFn: (ott: string) => profileApi.linkSteam(ott),
        onSuccess: showProfile,
    });

// Administration

export const useCreateUser = () =>
    useMutation({
        mutationFn: (request: CreateUserRequest) => adminUsersApi.create(request),
        onSuccess: invalidateAdminUsers,
        onError: error => notifyError(error),
    });

export const useUpdateUser = (id: number) =>
    useMutation({
        mutationFn: (request: UpdateUserRequest) => adminUsersApi.update(id, request),
        onSuccess: invalidateAdminUsers,
        onError: error => notifyError(error),
    });

export const useResetUserPassword = (id: number) =>
    useMutation({
        mutationFn: (password: string) => adminUsersApi.resetPassword(id, password),
        onSuccess: invalidateAdminUsers,
        onError: error => notifyError(error),
    });

export const useSaveUserIdentity = (id: number) =>
    useMutation({
        mutationFn: ({provider, key}: { provider: AuthSource; key: string }) => key.trim()
            ? adminUsersApi.setIdentity(id, provider, key)
            : adminUsersApi.removeIdentity(id, provider),
        onSuccess: invalidateAdminUsers,
        onError: error => notifyError(error),
    });

export const useDeleteUser = () =>
    useMutation({
        mutationFn: (id: number) => adminUsersApi.remove(id),
        onSuccess: invalidateAdminUsers,
        onError: error => notifyError(error),
    });
