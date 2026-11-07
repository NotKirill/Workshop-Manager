import {useMutation} from "@tanstack/react-query";
import queryClient from "@/app/queryClient.ts";
import {collectionsQueryKey} from "@/features/collections/queries.ts";
import type {CollectionKind} from "@/features/collections/types.ts";
import {gamesQueryKey} from "@/features/games/queries.ts";
import itemsApi from "@/features/items/api.ts";
import {itemQueryKey, itemsQueryKey} from "@/features/items/queries.ts";
import type {ItemDetail, LocalItemRequest, RelationType, UpdateItemRequest} from "@/features/items/types.ts";
import type {ModspaceType} from "@/lib/apiTypes.ts";
import {notifyError} from "@/lib/notify.ts";

const invalidateItem = (id: number) => void queryClient.invalidateQueries({queryKey: itemQueryKey(id)});

const invalidateItemLists = () => void queryClient.invalidateQueries({queryKey: itemsQueryKey});

const invalidateItemAndLists = (id: number) => {
    invalidateItem(id);
    invalidateItemLists();
};

// Registering and unregistering can create games or change their item counts
const invalidateItemListsAndGames = () => {
    invalidateItemLists();
    void queryClient.invalidateQueries({queryKey: gamesQueryKey});
};

// The server answers with the changed item: show it at once, refresh the lists
const showItem = (item: ItemDetail) => {
    queryClient.setQueryData(itemQueryKey(item.id), item);
    invalidateItemLists();
};

// One item

export const useUpdateItem = (id: number) =>
    useMutation({
        mutationFn: (request: UpdateItemRequest) => itemsApi.update(id, request),
        onSuccess: showItem,
        onError: error => notifyError(error, "Could not save"),
    });

export const useRefreshItem = (id: number) =>
    useMutation({
        mutationFn: () => itemsApi.refresh(id),
        onSuccess: showItem,
        onError: error => notifyError(error, "Refresh failed"),
    });

export const useUnregisterItem = (id: number) =>
    useMutation({
        mutationFn: () => itemsApi.unregister(id),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error),
    });

export const useRateItem = (id: number) =>
    useMutation({
        mutationFn: (score: number | null) => score === null ? itemsApi.clearRating(id) : itemsApi.rate(id, score),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

export const useSetItemCollections = (id: number, kind: CollectionKind) =>
    useMutation({
        mutationFn: (ids: number[]) => kind === "tags" ? itemsApi.setTags(id, ids) : itemsApi.setGroups(id, ids),
        onSuccess: () => {
            invalidateItemAndLists(id);
            void queryClient.invalidateQueries({queryKey: collectionsQueryKey(kind)});
        },
        onError: error => notifyError(error),
    });

// Registration

export const useRegisterExternalItems = () =>
    useMutation({
        mutationFn: ({items, autoBackup, includeDependencies}: {
            items: { modspace: ModspaceType; original_ids: string[] }[];
            autoBackup: boolean;
            includeDependencies: boolean;
        }) => itemsApi.registerExternal(items, autoBackup, includeDependencies),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error, "Registration failed"),
    });

export const useRegisterLocalItem = () =>
    useMutation({
        mutationFn: (request: LocalItemRequest) => itemsApi.registerLocal(request),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error, "Could not create item"),
    });

// Relations and dependencies

export const useAddRelation = (id: number) =>
    useMutation({
        mutationFn: ({targetId, relation}: { targetId: number; relation: RelationType }) =>
            itemsApi.addRelation(id, targetId, relation),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error),
    });

export const useRemoveRelation = (id: number) =>
    useMutation({
        mutationFn: ({targetId, relation}: { targetId: number; relation: RelationType }) =>
            itemsApi.removeRelation(id, targetId, relation),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error),
    });

export const useRegisterDependencies = (id: number) =>
    useMutation({
        mutationFn: () => itemsApi.registerDependencies(id),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

export const useReviewDependencies = (id: number) =>
    useMutation({
        mutationFn: ({add, remove}: { add: string[]; remove: number[] }) => itemsApi.reviewDependencies(id, add, remove),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

// Backups

export const useRequestBackup = (id: number) =>
    useMutation({
        mutationFn: () => itemsApi.requestBackup(id),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

export const useDownloadRevision = (id: number) =>
    useMutation({
        mutationFn: (revision: number) => itemsApi.downloadLink(id, revision),
        onSuccess: link => {
            globalThis.location.href = link.url;
        },
        onError: error => notifyError(error),
    });

export const useDeleteRevision = (id: number) =>
    useMutation({
        mutationFn: (revision: number) => itemsApi.deleteRevision(id, revision),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

export const useDeleteAllContent = (id: number) =>
    useMutation({
        mutationFn: () => itemsApi.deleteAllContent(id),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

export const useSetMaxRevisions = (id: number) =>
    useMutation({
        mutationFn: (maxRevisions: number) => itemsApi.setMaxRevisions(id, maxRevisions),
        onSuccess: () => invalidateItemAndLists(id),
        onError: error => notifyError(error),
    });

// Media

export const useUploadMedia = (id: number) =>
    useMutation({
        mutationFn: ({file, purpose}: { file: File; purpose: string }) => itemsApi.uploadMedia(id, file, purpose),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error, "Upload failed"),
    });

export const useAddEmbed = (id: number) =>
    useMutation({
        mutationFn: ({url, purpose}: { url: string; purpose: string }) => itemsApi.addEmbed(id, url, purpose),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error),
    });

export const useAddMediaLink = (id: number) =>
    useMutation({
        mutationFn: ({mediaItemId, purpose}: { mediaItemId: number; purpose: string }) =>
            itemsApi.addMediaLink(id, mediaItemId, purpose),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error),
    });

export const useSetMediaHidden = (id: number) =>
    useMutation({
        mutationFn: ({linkId, hidden}: { linkId: number; hidden: boolean }) =>
            itemsApi.updateMedia(id, linkId, {hidden}),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error),
    });

export const useDeleteMedia = (id: number) =>
    useMutation({
        mutationFn: (linkId: number) => itemsApi.deleteMedia(id, linkId),
        onSuccess: () => invalidateItem(id),
        onError: error => notifyError(error),
    });

// Many items (library selection)

export const useBulkUnregister = () =>
    useMutation({
        mutationFn: (ids: number[]) => itemsApi.bulkUnregister(ids),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error),
    });

export const useBulkAutoBackup = () =>
    useMutation({
        mutationFn: ({ids, enabled}: { ids: number[]; enabled: boolean }) => itemsApi.bulkAutoBackup(ids, enabled),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error),
    });

export const useBulkRefresh = () =>
    useMutation({
        mutationFn: (ids: number[]) => itemsApi.bulkRefresh(ids),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error),
    });

export const useBulkBackup = () =>
    useMutation({
        mutationFn: (ids: number[]) => itemsApi.bulkBackup(ids),
        onSuccess: invalidateItemListsAndGames,
        onError: error => notifyError(error),
    });
