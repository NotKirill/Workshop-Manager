import type {
    AuthRequest,
    AuthResponse,
    AuthSuccessRequest,
    LogoutRequest,
    PasswordChangeRequest,
    RefreshRequest
} from "@/features/auth/types.ts";
import {publicApi} from "@/app/axios.ts";


export const authApi = {
    login: async (request: AuthRequest): Promise<AuthResponse> => {
        const response = await publicApi.post<AuthResponse>(
            "/auth/login", request
        );

        return response.data;
    },

    loginSuccess: async (request: AuthSuccessRequest): Promise<AuthResponse> => {
        const response = await publicApi.post<AuthResponse>(
            "/auth/login-success", request
        );

        return response.data;
    },

    loginBySteam: async (state: string): Promise<{ url: string }> => {
        const response = await publicApi.get<{ url: string }>("/auth/steam", {params: {state}});

        return response.data;
    },

    refresh: async (request: RefreshRequest): Promise<AuthResponse> => {
        const response = await publicApi.post<AuthResponse>(
            "/auth/refresh", request
        );

        return response.data;
    },

    changePassword: async (request: PasswordChangeRequest): Promise<void> => {
        await publicApi.post("/auth/password", request);
    },

    logout: async (request: LogoutRequest): Promise<void> => {
        await publicApi.post("/auth/logout", request);
    },
};
export default authApi;
