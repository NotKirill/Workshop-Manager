import {privateApi} from "@/app/axios.ts";
import type {BulkResult} from "@/lib/apiTypes.ts";
import type {Collection, CollectionKind} from "@/features/collections/types.ts";

export const collectionsApi = {
    list: async (kind: CollectionKind, gameId?: number): Promise<Collection[]> =>
        (await privateApi.get<Collection[]>(`/${kind}`, {params: gameId ? {game_id: gameId} : {}})).data,

    create: async (kind: CollectionKind, name: string, gameId: number): Promise<Collection> =>
        (await privateApi.post<Collection>(`/${kind}`, {name, game_id: gameId})).data,

    rename: async (kind: CollectionKind, id: number, name: string): Promise<Collection> =>
        (await privateApi.patch<Collection>(`/${kind}/${id}`, {name})).data,

    remove: async (kind: CollectionKind, id: number): Promise<void> => {
        await privateApi.delete(`/${kind}/${id}`);
    },

    addItems: async (kind: CollectionKind, id: number, itemIds: number[]): Promise<BulkResult> =>
        (await privateApi.post<BulkResult>(`/${kind}/${id}/items`, {item_ids: itemIds})).data,

    removeItems: async (kind: CollectionKind, id: number, itemIds: number[]): Promise<BulkResult> =>
        (await privateApi.post<BulkResult>(`/${kind}/${id}/items/remove`, {item_ids: itemIds})).data,
};

export default collectionsApi;
