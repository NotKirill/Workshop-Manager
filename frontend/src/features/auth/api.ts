import type {AuthRequest, AuthResponse, LogoutRequest, MeResponse, RefreshRequest} from "@/features/auth/types.ts";
import {privateApi, publicApi} from "@/app/axios.ts";


export const authApi = {
    login: async (request: AuthRequest): Promise<AuthResponse> => {
        const response = await publicApi.post<AuthResponse>(
            "/auth/login", request
        );

        return response.data;
    },

    // TODO loginBySteam

    refresh: async (request: RefreshRequest): Promise<AuthResponse> => {
        const response = await publicApi.post<AuthResponse>(
            "/auth/refresh", request
        );

        return response.data;
    },

    logout: async (request: LogoutRequest): Promise<void> => {
        await publicApi.post("/auth/logout", request);
    },

    me: async (): Promise<MeResponse> => {
        const response = await privateApi.get<MeResponse>("/me");

        return response.data;
    },
};
export default authApi;
