import type {UseMutationResult} from "@tanstack/react-query";

export interface AuthResponse {
    access_token: string;
    refresh_token: string;
}

export interface AuthRequest {
    login: string;
    password: string;
}

export interface AuthSuccessRequest {
    ott: string;
}

export interface RefreshRequest {
    refresh_token: string;
}

export interface PasswordChangeRequest {
    login: string;
    current_password: string;
    new_password: string;
}

export interface LogoutRequest {
    refresh_token: string;
}

export interface AuthProvider {
    id: string;
    name: string;
    icon: string;
    redirectHook: () => UseMutationResult<{ url: string }, Error, void, unknown>;
}