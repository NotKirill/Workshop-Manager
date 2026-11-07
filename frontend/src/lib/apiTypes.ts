export type ModspaceType = "LOCAL" | "UNKNOWN" | "STEAM";

export type ContentJobType = "BACKUP" | "DELETE_REVISION" | "DELETE_ALL";

export type ContentJobStatus = "PENDING" | "RUNNING" | "SUCCEEDED" | "FAILED" | "CANCELLED";

export type ItemLifecycleState = "ACTIVE" | "ORPHANED" | "DELETING" | "FAILED";

export interface ApiError {
    status: number;
    code: string;
    field_errors?: Record<string, string>;
    error_uuid?: string;
}

export interface NamedRef {
    id: number;
    name: string;
}

export interface BulkResult {
    affected: number;
}
