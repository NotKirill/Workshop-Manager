import {privateApi} from "@/app/axios.ts";
import type {BulkResult, ModspaceType, NamedRef} from "@/lib/apiTypes.ts";
import type {ContentSummary, ContentUpload, ItemDetail, ItemPage, LocalItemRequest, MediaEntry, RegistrationResponse, RelationType, SearchHelp, UpdateItemRequest} from "@/features/items/types.ts";

export const itemsApi = {
    search: async (searchQuery: string, page: number): Promise<ItemPage> =>
        (await privateApi.post<ItemPage>("/items/search", {search_query: searchQuery, page})).data,

    searchHelp: async (): Promise<SearchHelp> =>
        (await privateApi.get<SearchHelp>("/items/search/help")).data,

    get: async (id: number): Promise<ItemDetail> =>
        (await privateApi.get<ItemDetail>(`/items/${id}`)).data,

    registerExternal: async (items: { modspace: ModspaceType; original_ids: string[] }[],
                             autoBackup: boolean,
                             includeDependencies: boolean): Promise<RegistrationResponse> =>
        (await privateApi.post<RegistrationResponse>("/items/register-external", {
            items,
            auto_backup: autoBackup,
            include_dependencies: includeDependencies,
        }, {timeout: 120000})).data,

    registerLocal: async (request: LocalItemRequest): Promise<RegistrationResponse> =>
        (await privateApi.post<RegistrationResponse>("/items/register-local", request)).data,

    registerDependencies: async (id: number, autoBackup?: boolean): Promise<RegistrationResponse> =>
        (await privateApi.post<RegistrationResponse>(`/items/${id}/dependencies/register`,
            {auto_backup: autoBackup ?? null}, {timeout: 120000})).data,

    reviewDependencies: async (id: number, add: string[], remove: number[]): Promise<RegistrationResponse> =>
        (await privateApi.post<RegistrationResponse>(`/items/${id}/dependencies/review`, {add, remove},
            {timeout: 120000})).data,

    update: async (id: number, request: UpdateItemRequest): Promise<ItemDetail> =>
        (await privateApi.put<ItemDetail>(`/items/${id}`, request)).data,

    unregister: async (id: number): Promise<void> => {
        await privateApi.delete(`/items/${id}`);
    },

    refresh: async (id: number): Promise<ItemDetail> =>
        (await privateApi.post<ItemDetail>(`/items/${id}/refresh`, null, {timeout: 60000})).data,

    bulkUnregister: async (itemIds: number[]): Promise<BulkResult> =>
        (await privateApi.post<BulkResult>("/items/bulk/unregister", {item_ids: itemIds})).data,

    bulkRefresh: async (itemIds: number[]): Promise<BulkResult> =>
        (await privateApi.post<BulkResult>("/items/bulk/refresh", {item_ids: itemIds}, {timeout: 120000})).data,

    bulkAutoBackup: async (itemIds: number[], autoBackup: boolean): Promise<BulkResult> =>
        (await privateApi.post<BulkResult>("/items/bulk/auto-backup",
            {item_ids: itemIds, auto_backup: autoBackup})).data,

    bulkBackup: async (itemIds: number[]): Promise<BulkResult> =>
        (await privateApi.post<BulkResult>("/items/bulk/backup", {item_ids: itemIds})).data,

    rate: async (id: number, score: number): Promise<void> => {
        await privateApi.put(`/items/${id}/rating`, {score});
    },

    clearRating: async (id: number): Promise<void> => {
        await privateApi.delete(`/items/${id}/rating`);
    },

    setTags: async (id: number, ids: number[]): Promise<NamedRef[]> =>
        (await privateApi.put<NamedRef[]>(`/items/${id}/tags`, {ids})).data,

    setGroups: async (id: number, ids: number[]): Promise<NamedRef[]> =>
        (await privateApi.put<NamedRef[]>(`/items/${id}/groups`, {ids})).data,

    addRelation: async (id: number, targetId: number, relation: RelationType): Promise<void> => {
        await privateApi.post(`/items/${id}/relations`, {target_id: targetId, relation});
    },

    removeRelation: async (id: number, targetId: number, relation: RelationType): Promise<void> => {
        await privateApi.delete(`/items/${id}/relations/${targetId}/${relation}`);
    },

    content: async (id: number): Promise<ContentSummary> =>
        (await privateApi.get<ContentSummary>(`/items/${id}/content`)).data,

    requestBackup: async (id: number): Promise<void> => {
        await privateApi.post(`/items/${id}/content/backup`);
    },

    startContentUpload: async (id: number, fileName: string, size: number): Promise<ContentUpload> =>
        (await privateApi.post<ContentUpload>(`/items/${id}/content/uploads`, {file_name: fileName, size})).data,

    contentUpload: async (id: number, uploadId: number): Promise<ContentUpload> =>
        (await privateApi.get<ContentUpload>(`/items/${id}/content/uploads/${uploadId}`)).data,

    uploadContentPart: async (id: number, uploadId: number, offset: number, part: Blob,
                              onProgress?: (loaded: number) => void): Promise<ContentUpload> =>
        (await privateApi.patch<ContentUpload>(`/items/${id}/content/uploads/${uploadId}`, part, {
            headers: {"Content-Type": "application/octet-stream", "Upload-Offset": String(offset)},
            timeout: 0,
            onUploadProgress: event => onProgress?.(event.loaded),
        })).data,

    downloadLink: async (id: number, revision: number): Promise<{ url: string; expires_at: number }> =>
        (await privateApi.post<{ url: string; expires_at: number }>(
            `/items/${id}/content/revisions/${revision}/link`)).data,

    deleteRevision: async (id: number, revision: number): Promise<void> => {
        await privateApi.delete(`/items/${id}/content/revisions/${revision}`);
    },

    deleteAllContent: async (id: number): Promise<void> => {
        await privateApi.delete(`/items/${id}/content`);
    },

    setMaxRevisions: async (id: number, maxRevisions: number): Promise<void> => {
        await privateApi.put(`/items/${id}/content/settings`, {max_revisions: maxRevisions});
    },

    uploadMedia: async (id: number, file: File, purpose: string): Promise<MediaEntry> => {
        const form = new FormData();
        form.append("file", file);
        form.append("purpose", purpose);
        return (await privateApi.post<MediaEntry>(`/items/${id}/media/upload`, form, {
            headers: {"Content-Type": "multipart/form-data"},
            timeout: 0,
        })).data;
    },

    addEmbed: async (id: number, url: string, purpose: string): Promise<MediaEntry> =>
        (await privateApi.post<MediaEntry>(`/items/${id}/media/embed`, {url, purpose})).data,

    addMediaLink: async (id: number, mediaItemId: number, purpose: string): Promise<MediaEntry> =>
        (await privateApi.post<MediaEntry>(`/items/${id}/media/links`, {media_item_id: mediaItemId, purpose})).data,

    updateMedia: async (id: number, linkId: number, changes: { purpose?: string; hidden?: boolean }): Promise<void> => {
        await privateApi.patch(`/items/${id}/media/${linkId}`, changes);
    },

    deleteMedia: async (id: number, linkId: number): Promise<void> => {
        await privateApi.delete(`/items/${id}/media/${linkId}`);
    },
};

export default itemsApi;
