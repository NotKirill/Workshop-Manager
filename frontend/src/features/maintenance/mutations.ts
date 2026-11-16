import {useMutation} from "@tanstack/react-query";
import queryClient from "@/app/queryClient.ts";
import {maintenanceApi} from "@/features/maintenance/api.ts";
import {maintenanceQueryKey} from "@/features/maintenance/queries.ts";
import {notifyError} from "@/lib/notify.ts";

const invalidateMaintenance = () => void queryClient.invalidateQueries({queryKey: maintenanceQueryKey});

// Runs

export const useRunCleanup = () =>
    useMutation({
        mutationFn: maintenanceApi.runCleanup,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRunSync = () =>
    useMutation({
        mutationFn: maintenanceApi.runSync,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRunAuthorRefresh = () =>
    useMutation({
        mutationFn: maintenanceApi.runAuthorRefresh,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRunGameLookup = () =>
    useMutation({
        mutationFn: maintenanceApi.runGameLookup,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRunContentJobs = () =>
    useMutation({
        mutationFn: maintenanceApi.runContentJobs,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRunMediaSyncs = () =>
    useMutation({
        mutationFn: maintenanceApi.runMediaSyncs,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

// Retries

export const useRetryMediaSync = () =>
    useMutation({
        mutationFn: maintenanceApi.retryMediaSync,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRetryCleanup = () =>
    useMutation({
        mutationFn: maintenanceApi.retryCleanup,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });

export const useRetryContentJob = () =>
    useMutation({
        mutationFn: maintenanceApi.retryContentJob,
        onSuccess: invalidateMaintenance,
        onError: error => notifyError(error),
    });
