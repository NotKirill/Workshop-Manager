import {privateApi} from "@/app/axios.ts";
import type {ContentJobStatus} from "@/lib/apiTypes.ts";
import type {AuthorRefreshRun, FailedCleanup, GameLookupRun, MaintenanceContentJob, MaintenanceOverview, MediaSync, SyncRun} from "@/features/maintenance/types.ts";

export const maintenanceApi = {
    overview: async (): Promise<MaintenanceOverview> =>
        (await privateApi.get<MaintenanceOverview>("/admin/maintenance")).data,

    failedCleanups: async (): Promise<FailedCleanup[]> =>
        (await privateApi.get<FailedCleanup[]>("/admin/maintenance/cleanups/failed")).data,

    retryCleanup: async (internalId: number): Promise<void> => {
        await privateApi.post(`/admin/maintenance/cleanups/${internalId}/retry`);
    },

    runCleanup: async (): Promise<{ marked: number; deleted: number; failed: number }> =>
        (await privateApi.post<{ marked: number; deleted: number; failed: number }>(
            "/admin/maintenance/cleanups/run", null, {timeout: 120000})).data,

    runSync: async (): Promise<SyncRun> =>
        (await privateApi.post<SyncRun>("/admin/maintenance/sync/run", null, {timeout: 120000})).data,

    runAuthorRefresh: async (): Promise<AuthorRefreshRun> =>
        (await privateApi.post<AuthorRefreshRun>(
            "/admin/maintenance/authors/refresh/run", null, {timeout: 120000})).data,

    runGameLookup: async (): Promise<GameLookupRun> =>
        (await privateApi.post<GameLookupRun>("/admin/maintenance/games/lookup/run", null, {timeout: 120000})).data,

    contentJobs: async (statuses: ContentJobStatus[]): Promise<MaintenanceContentJob[]> =>
        (await privateApi.get<MaintenanceContentJob[]>("/admin/maintenance/content-jobs", {
            params: statuses.length > 0 ? {status: statuses.join(",")} : {},
        })).data,

    retryContentJob: async (jobId: number): Promise<void> => {
        await privateApi.post(`/admin/maintenance/content-jobs/${jobId}/retry`);
    },

    givenUpMediaSyncs: async (): Promise<MediaSync[]> =>
        (await privateApi.get<MediaSync[]>("/admin/maintenance/media-syncs/given-up")).data,

    retryMediaSync: async (sync: MediaSync): Promise<void> => {
        await privateApi.post(`/admin/maintenance/media-syncs/${sync.entity}/${sync.entity_id}/retry`);
    },

    runMediaSyncs: async (): Promise<{ processed: number }> =>
        (await privateApi.post<{ processed: number }>("/admin/maintenance/media-syncs/run", null,
            {timeout: 0})).data,

    runContentJobs: async (): Promise<{ processed: number }> =>
        (await privateApi.post<{ processed: number }>("/admin/maintenance/content-jobs/run", null,
            {timeout: 0})).data,
};
