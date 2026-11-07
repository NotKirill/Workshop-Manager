import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import itemsApi from "@/features/items/api.ts";

export const itemsQueryKey = ["items"] as const;

export const itemQueryKey = (id: number) => ["item", id] as const;

export const searchHelpQueryKey = ["search-help"] as const;

export const useItemSearch = (query: string) =>
    useInfiniteQuery({
        queryKey: [...itemsQueryKey, "search", query],
        queryFn: ({pageParam}) => itemsApi.search(query, pageParam),
        initialPageParam: 0,
        getNextPageParam: last => (last.page + 1 < last.total_pages ? last.page + 1 : undefined),
        retry: false,
    });

export const useRelationCandidates = (search: string, enabled: boolean) =>
    useQuery({
        queryKey: [...itemsQueryKey, "relation-search", search],
        queryFn: () => itemsApi.search(search.trim() ? `${search.trim()} size:20` : "size:20", 0),
        enabled,
    });

export const useItem = (id: number) =>
    useQuery({
        queryKey: itemQueryKey(id),
        queryFn: () => itemsApi.get(id),
        enabled: Number.isFinite(id),
        refetchInterval: query => (query.state.data?.content.summary.active_job ? 5000 : false),
    });

export const useSearchHelp = () =>
    useQuery({queryKey: searchHelpQueryKey, queryFn: itemsApi.searchHelp, staleTime: Infinity});
