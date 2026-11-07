import type {ModspaceType} from "@/lib/apiTypes.ts";

export interface Game {
    id: number;
    name: string;
    slug: string;
    name_pending: boolean;
    cover_url: string | null;
    modspace_ids: { modspace: ModspaceType; external_id: string }[];
    compatible_game_ids: number[];
    item_count: number;
}
