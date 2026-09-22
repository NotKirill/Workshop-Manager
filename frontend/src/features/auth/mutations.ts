import {useMutation} from "@tanstack/react-query";
import axios from "axios";
import authService from "@/features/auth/service.ts";
import type {AuthRequest, AuthSuccessRequest} from "@/features/auth/types.ts";
import authApi from "@/features/auth/api.ts";
import tokenStorage from "@/app/tokenStorage.ts";
import steamOpenIdService from "@/features/auth/providers/steam/service.ts";

const clearOnUnauthorized = (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
        authService.clearAuth();
    }
};

// Long TODO maybe other 'onError' handler?
export const useLogin = () =>
    useMutation({
        mutationFn: (request: AuthRequest) =>
            authApi.login(request),

        onSuccess: authService.saveAuth,

        onError: clearOnUnauthorized,
    });

// Long TODO maybe other 'onError' handler?
export const useLoginSuccess = () =>
    useMutation({
        mutationFn: (request: AuthSuccessRequest) =>
            authApi.loginSuccess(request),

        onSuccess: authService.saveAuth,

        onError: clearOnUnauthorized,
    });

export const useSteamLogin = () =>
    useMutation({
        mutationFn: () =>
            authApi.loginBySteam(steamOpenIdService.createLoginState()),

        onSuccess: (data) => steamOpenIdService.redirectToSteamLogin(data.url),
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