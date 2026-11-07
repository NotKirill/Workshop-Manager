import {useQuery} from "@tanstack/react-query";
import collectionsApi from "@/features/collections/api.ts";
import type {CollectionKind} from "@/features/collections/types.ts";

export const collectionsQueryKey = (kind: CollectionKind) => [kind] as const;

export const useCollections = (kind: CollectionKind, gameId: number | null | undefined, enabled = true) =>
    useQuery({
        queryKey: [...collectionsQueryKey(kind), gameId],
        queryFn: () => collectionsApi.list(kind, gameId ?? undefined),
        enabled: enabled && gameId != null,
    });
