import type {ContentJobStatus, ContentJobType, ModspaceType, NamedRef} from "@/lib/apiTypes.ts";

export type ItemSourceStatus = "AVAILABLE" | "HIDDEN" | "UNAVAILABLE";

export type RelationType = "PARENT" | "ALTERNATE" | "DEPENDENCY";

export interface ItemSummary {
    id: number;
    internal_id: number;
    title: string | null;
    description_snippet: string | null;
    title_overridden: boolean;
    description_overridden: boolean;
    modspace: ModspaceType;
    original_id: string;
    game: NamedRef;
    auto_backup: boolean;
    rating: number | null;
    source_status: ItemSourceStatus;
    file_size: number | null;
    source_time_updated: string | null;
    time_registered: string;
    tags: NamedRef[];
    backup: {
        revision_count: number;
        latest_backup_at: string | null;
        active_job: ContentJobType | null;
    } | null;
    preview_url: string | null;
}

export interface ItemPage {
    items: ItemSummary[];
    page: number;
    size: number;
    total_elements: number;
    total_pages: number;
}

export interface ContentRevision {
    revision: number;
    file_name: string;
    file_size: number;
    sha256: string | null;
    source_version: string | null;
    uploaded: boolean;
    time_created: string;
}

export interface ContentUpload {
    upload_id: number;
    file_name: string;
    size: number;
    received: number;
    chunk_size: number;
    revision: ContentRevision | null;
}

export interface ContentJobInfo {
    id: number;
    type: ContentJobType;
    status: ContentJobStatus;
    attempts: number;
    next_attempt_at: string;
    error_code: ContentJobError | null;
}

export type ContentJobError = "DOWNLOAD_UNAVAILABLE" | "NOT_DOWNLOADABLE" | "DOWNLOAD_TIMEOUT" | "DOWNLOAD_FAILED"
    | "INTERNAL_ERROR";

export interface ContentSummary {
    max_revisions: number;
    revisions: ContentRevision[];
    active_job: ContentJobInfo | null;
    last_failed_job: ContentJobInfo | null;
}

export interface MediaEntry {
    id: number;
    media_item_id: number;
    type: "FILE" | "EMBED";
    url: string | null;
    provider: "UNKNOWN" | "YOUTUBE" | "SKETCHFAB" | null;
    external_id: string | null;
    file_name: string | null;
    purpose: string;
    sort_order: number;
    owned: boolean;
    hidden: boolean;
}

export interface SteamDetails {
    published_file_id: string;
    consumer_app_id: number;
    creator_app_id: number;
    visibility: number;
    subscriptions: number;
    favorited: number;
    lifetime_subscriptions: number;
    lifetime_favorited: number;
    votes_up: number | null;
    votes_down: number | null;
    score: number | null;
    tags: string[];
    previews: { type: "IMAGE" | "YOUTUBE" | "SKETCHFAB" | "OTHER"; url: string; external_id: string | null }[];
    links: { key: string; value: string }[];
    content_descriptor_ids: number[];
    maybe_inappropriate_sex: boolean;
    maybe_inappropriate_violence: boolean;
    short_description: string | null;
}

export interface ItemDetail {
    id: number;
    internal_id: number;
    modspace: ModspaceType;
    original_id: string;
    source_status: ItemSourceStatus;
    title: string | null;
    description: string | null;
    title_overridden: boolean;
    description_overridden: boolean;
    original: { title: string | null; description: string | null } | null;
    personal: {
        title: string | null;
        description: string | null;
        user_commentary: string | null;
        auto_backup: boolean;
        time_registered: string;
        dependencies_need_review: boolean | null;
    };
    game: NamedRef;
    author: { id: number; name: string } | null;
    file_size: number | null;
    source_time_created: string | null;
    source_time_updated: string | null;
    time_synced: string | null;
    rating: number | null;
    tags: NamedRef[];
    groups: NamedRef[];
    relations: { item_id: number; title: string | null; relation: RelationType; direction: "OUTGOING" | "INCOMING" }[];
    dependencies: { original_id: string; personal_id: number | null; title: string | null }[];
    steam: SteamDetails | null;
    content: {
        summary: ContentSummary;
        can_backup: boolean;
        can_upload: boolean;
        can_delete: boolean;
    };
    media: MediaEntry[];
}

export type RegistrationStatus = "REGISTERED" | "ALREADY_REGISTERED" | "INVALID" | "FAILED";

export interface RegistrationResult {
    modspace: ModspaceType;
    original_id: string;
    status: RegistrationStatus;
    personal_id: number | null;
    internal_id: number | null;
    title: string | null;
    reason: string | null;
    dependency_of: number | null;
}

export interface RegistrationResponse {
    results: RegistrationResult[];
}

export interface SearchHelpField {
    tag: string;
    type: string;
    sortable: boolean;
    values: string[];
}

export interface SearchHelp {
    fields: SearchHelpField[];
    examples: string[];
}

export interface UpdateItemRequest {
    title: string | null;
    description: string | null;
    user_commentary: string | null;
    auto_backup: boolean;
}

export interface LocalItemRequest {
    game_id?: number | null;
    game_name?: string | null;
    title: string;
    description?: string | null;
    user_commentary?: string | null;
}
