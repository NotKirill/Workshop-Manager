import {useQuery} from "@tanstack/react-query";
import gamesApi from "@/features/games/api.ts";

export const gamesQueryKey = ["games"] as const;

export const UNKNOWN_GAME_ID = -1;

export const useGames = () => useQuery({queryKey: gamesQueryKey, queryFn: gamesApi.list, staleTime: 30_000});
