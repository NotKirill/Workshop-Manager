import {privateApi} from "@/app/axios.ts";
import type {Game} from "@/features/games/types.ts";

export const gamesApi = {
    list: async (): Promise<Game[]> => (await privateApi.get<Game[]>("/games")).data,

    create: async (name: string): Promise<Game> => (await privateApi.post<Game>("/games", {name})).data,

    rename: async (id: number, name: string): Promise<Game> =>
        (await privateApi.patch<Game>(`/games/${id}`, {name})).data,

    addCompatibility: async (id: number, targetGameId: number): Promise<void> => {
        await privateApi.post(`/games/${id}/compatibility`, {target_game_id: targetGameId});
    },

    removeCompatibility: async (id: number, targetGameId: number): Promise<void> => {
        await privateApi.delete(`/games/${id}/compatibility/${targetGameId}`);
    },
};

export default gamesApi;
