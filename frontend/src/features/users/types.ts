export type Permission = "ITEMS_READ" | "ITEMS_WRITE" | "ITEMS_REGISTER" | "CONTENT_IMPORT" | "MEDIA_UPLOAD"
    | "USERS_MANAGE" | "GAMES_MANAGE" | "CONTENT_MANAGE" | "MAINTENANCE_MANAGE";

export type AuthSource = "LOCAL_USERNAME" | "LOCAL_EMAIL" | "STEAM";

export interface Identity {
    provider: AuthSource;
    key: string;
}

export interface Profile {
    id: number;
    name: string;
    time_created: string;
    has_password: boolean;
    permissions: PermissionInfo[];
    identities: Identity[];
    item_count: number;
}

export interface AdminUser {
    id: number;
    name: string;
    time_created: string;
    enabled: boolean;
    account_non_locked: boolean;
    has_password: boolean;
    permissions: Permission[];
    identities: Identity[];
    item_count: number;
}

export interface PermissionInfo {
    permission: Permission;
    name: string;
    description: string | null;
}

export interface CreateUserRequest {
    name?: string | null;
    password?: string | null;
    identities: Partial<Record<AuthSource, string>>;
    permissions: Permission[];
}

export interface UpdateUserRequest {
    name?: string | null;
    enabled?: boolean | null;
    account_non_locked?: boolean | null;
    permissions?: Permission[] | null;
}
