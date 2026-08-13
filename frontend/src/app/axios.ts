import axios, {type InternalAxiosRequestConfig} from "axios";
import tokenStorage from "@/app/tokenStorage.ts";
import type {AuthResponse} from "@/features/auth/types.ts";
import authApi from "@/features/auth/api.ts";
import authService from "@/features/auth/service.ts";

interface CustomRequestConfig extends InternalAxiosRequestConfig {
    _retry?: boolean;
}

export const publicApi = axios.create({
    baseURL: "/api",
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

export const privateApi = axios.create({
    baseURL: "/api",
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
    },
});

privateApi.interceptors.request.use(
    config => {
        const token = tokenStorage.getAccessToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        const rejectError = error instanceof Error ? error : new Error(String(error));
        return Promise.reject(rejectError);
    }
);

let refreshPromise: Promise<AuthResponse> | null = null;

privateApi.interceptors.response.use(
    response => response,
    async (error: unknown) => {
        if (!axios.isAxiosError(error)) {
            throw error instanceof Error ? error : new Error(String(error));
        }

        const originalRequest = error.config as CustomRequestConfig | undefined;

        if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
            originalRequest._retry = true;

            const refreshToken = tokenStorage.getRefreshToken();
            if (!refreshToken) throw error;

            try {
                refreshPromise ??= authApi.refresh({refresh_token: refreshToken}).finally(() => {
                    refreshPromise = null;
                });
                const tokens = await refreshPromise;

                authService.saveAuth(tokens);
                originalRequest.headers.Authorization = `Bearer ${tokens.access_token}`;

                return privateApi(originalRequest);
            } catch {
                // TODO difference between errors???
                authService.clearAuth();
                throw error;
                // console.error('Failed to refresh token');
            }
        }

        throw error;
    }
);