import {useMutation} from "@tanstack/react-query";
import axios from "axios";
import authService from "@/features/auth/service.ts";
import type {AuthRequest} from "@/features/auth/types.ts";
import authApi from "@/features/auth/api.ts";
import tokenStorage from "@/app/tokenStorage.ts";

const clearOnUnauthorized = (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
        authService.clearAuth();
    }
};

export const useLogin = () =>
    useMutation({
        mutationFn: (request: AuthRequest) =>
            authApi.login(request),

        onSuccess: authService.saveAuth,

        onError: clearOnUnauthorized,
    });

export const useRefresh = () =>
    useMutation({
        mutationFn: async () => {
            const token = tokenStorage.getRefreshToken();

            if (!token) {
                throw new Error("No refresh token");
            }

            return authApi.refresh({refresh_token: token});
        },

        onSuccess: authService.saveAuth,

        onError: clearOnUnauthorized,
    });

export const useLogout = () =>
    useMutation({
        mutationFn: async () => {
            const token = tokenStorage.getRefreshToken();

            if (token) {
                await authApi.logout({refresh_token: token});
            }
        },

        onSettled: () => {
            authService.clearAuth();
        },
    });

export const useMeRequest = () =>
    useMutation({
        mutationFn: () =>
            authApi.me(),

        onError: clearOnUnauthorized,
    });