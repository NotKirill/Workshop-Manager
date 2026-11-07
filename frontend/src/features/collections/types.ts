export interface Collection {
    id: number;
    name: string;
    game_id: number;
    item_count: number;
}

export type CollectionKind = "tags" | "groups";
