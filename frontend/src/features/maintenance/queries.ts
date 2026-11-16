import {useQuery} from "@tanstack/react-query";
import {maintenanceApi} from "@/features/maintenance/api.ts";

export const maintenanceQueryKey = ["admin", "maintenance"] as const;

export const useMaintenanceOverview = () =>
    useQuery({queryKey: [...maintenanceQueryKey, "overview"], queryFn: maintenanceApi.overview});

export const useFailedCleanups = () =>
    useQuery({queryKey: [...maintenanceQueryKey, "cleanups"], queryFn: maintenanceApi.failedCleanups});

export const useGivenUpMediaSyncs = () =>
    useQuery({queryKey: [...maintenanceQueryKey, "media-syncs"], queryFn: maintenanceApi.givenUpMediaSyncs});

export const useMaintenanceContentJobs = () =>
    useQuery({
        queryKey: [...maintenanceQueryKey, "content-jobs"],
        queryFn: () => maintenanceApi.contentJobs([]),
        refetchInterval: 15000,
    });
