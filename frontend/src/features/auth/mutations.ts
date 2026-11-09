import {useMutation} from "@tanstack/react-query";
import axios from "axios";
import authService from "@/features/auth/service.ts";
import type {AuthRequest, AuthResponse, AuthSuccessRequest, PasswordChangeRequest} from "@/features/auth/types.ts";
import authApi from "@/features/auth/api.ts";
import tokenStorage from "@/app/tokenStorage.ts";
import steamOpenIdService from "@/features/auth/providers/steam/service.ts";
import queryClient from "@/app/queryClient.ts";

const startSession = (response: AuthResponse) => {
    queryClient.clear();
    authService.saveAuth(response);
};

const clearOnUnauthorized = (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
        authService.clearAuth();
    }
};

export const useLogin = () =>
    useMutation({
        mutationFn: (request: AuthRequest) =>
            authApi.login(request),

        onSuccess: startSession,

        onError: clearOnUnauthorized,
    });

export const useLoginSuccess = () =>
    useMutation({
        mutationFn: (request: AuthSuccessRequest) =>
            authApi.loginSuccess(request),

        onSuccess: startSession,

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
            queryClient.clear();
        },
    });

export const useChangePasswordBySignIn = () =>
    useMutation({
        mutationFn: (request: PasswordChangeRequest) => authApi.changePassword(request),
    });
