import type {ContentJobStatus, ContentJobType, ItemLifecycleState, ModspaceType} from "@/lib/apiTypes.ts";

export interface MaintenanceOverview {
    steam_api_key_configured: boolean;
    steamcmd_configured: boolean;
    storage_type: string;
}

export interface FailedCleanup {
    internal_id: number;
    modspace: ModspaceType;
    original_id: string;
    state: ItemLifecycleState;
    attempts: number;
    last_error: string | null;
    orphaned_at: string | null;
}

export interface MediaSync {
    entity: "ITEM" | "USER" | "GAME" | "AUTHOR";
    entity_id: number;
    title: string | null;
    failures: number;
    last_error: string | null;
    time_synced: string | null;
}

export interface MaintenanceContentJob {
    id: number;
    internal_id: number;
    title: string | null;
    type: ContentJobType;
    status: ContentJobStatus;
    revision: number | null;
    attempts: number;
    next_attempt_at: string;
    last_error: string | null;
    note: string | null;
    time_created: string;
    time_finished: string | null;
}

export interface SyncRun {
    synced: number;
    unavailable: number;
    backupsQueued: number;
}

export interface AuthorRefreshRun {
    refreshed: number;
    failed: number;
}

export interface GameLookupRun {
    named: number;
    unknown: number;
    failed: number;
}
