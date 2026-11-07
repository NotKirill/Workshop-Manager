import {useMutation} from "@tanstack/react-query";
import queryClient from "@/app/queryClient.ts";
import collectionsApi from "@/features/collections/api.ts";
import {collectionsQueryKey} from "@/features/collections/queries.ts";
import type {CollectionKind} from "@/features/collections/types.ts";
import {itemsQueryKey} from "@/features/items/queries.ts";
import {notifyError} from "@/lib/notify.ts";

const invalidateCollections = (kind: CollectionKind) =>
    void queryClient.invalidateQueries({queryKey: collectionsQueryKey(kind)});

const invalidateCollectionsAndItems = (kind: CollectionKind) => {
    invalidateCollections(kind);
    void queryClient.invalidateQueries({queryKey: itemsQueryKey});
};

export const useCreateCollection = (kind: CollectionKind) =>
    useMutation({
        mutationFn: ({name, gameId}: { name: string; gameId: number }) => collectionsApi.create(kind, name, gameId),
        onSuccess: () => invalidateCollections(kind),
        onError: error => notifyError(error),
    });

export const useRenameCollection = (kind: CollectionKind, id: number) =>
    useMutation({
        mutationFn: (name: string) => collectionsApi.rename(kind, id, name),
        onSuccess: () => invalidateCollectionsAndItems(kind),
        onError: error => notifyError(error),
    });

export const useDeleteCollection = (kind: CollectionKind, id: number) =>
    useMutation({
        mutationFn: () => collectionsApi.remove(kind, id),
        onSuccess: () => invalidateCollectionsAndItems(kind),
        onError: error => notifyError(error),
    });

export const useChangeCollectionItems = (kind: CollectionKind) =>
    useMutation({
        mutationFn: async ({target, itemIds, mode}: {
            target: number | { name: string; gameId: number };
            itemIds: number[];
            mode: "add" | "remove";
        }) => {
            const id = typeof target === "number"
                ? target
                : (await collectionsApi.create(kind, target.name, target.gameId)).id;
            return mode === "add"
                ? collectionsApi.addItems(kind, id, itemIds)
                : collectionsApi.removeItems(kind, id, itemIds);
        },
        onSuccess: () => invalidateCollectionsAndItems(kind),
        onError: error => notifyError(error),
    });
