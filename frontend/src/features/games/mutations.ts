import {useMutation} from "@tanstack/react-query";
import queryClient from "@/app/queryClient.ts";
import gamesApi from "@/features/games/api.ts";
import {gamesQueryKey} from "@/features/games/queries.ts";
import {notifyError} from "@/lib/notify.ts";

const invalidateGames = () => void queryClient.invalidateQueries({queryKey: gamesQueryKey});

export const useRenameGame = (id: number) =>
    useMutation({
        mutationFn: (name: string) => gamesApi.rename(id, name),
        onSuccess: invalidateGames,
        onError: error => notifyError(error),
    });

export const useAddGameCompatibility = (id: number) =>
    useMutation({
        mutationFn: (targetGameId: number) => gamesApi.addCompatibility(id, targetGameId),
        onSuccess: invalidateGames,
        onError: error => notifyError(error),
    });

export const useRemoveGameCompatibility = (id: number) =>
    useMutation({
        mutationFn: (targetGameId: number) => gamesApi.removeCompatibility(id, targetGameId),
        onSuccess: invalidateGames,
        onError: error => notifyError(error),
    });
