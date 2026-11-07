import type {ModspaceType} from "@/lib/apiTypes.ts";

export const externalModspaces: {
    type: ModspaceType;
    label: string;
    // First group is the original id
    linkPattern: RegExp;
    idPattern: RegExp;
    itemUrl: (originalId: string) => string;
}[] = [
    {
        type: "STEAM",
        label: "Steam Workshop",
        linkPattern: /(?:steamcommunity\.com\/(?:sharedfiles|workshop)\/filedetails\/?\?(?:.*&)?id=|steam:\/\/url\/CommunityFilePage\/)(\d{1,20})/i,
        idPattern: /^\d{1,20}$/,
        itemUrl: originalId => `https://steamcommunity.com/sharedfiles/filedetails/?id=${encodeURIComponent(originalId)}`,
    },
];

export const itemUrl = (modspace: ModspaceType, originalId: string): string | null =>
    externalModspaces.find(external => external.type === modspace)?.itemUrl(originalId) ?? null;

export interface ParsedReferences {
    items: { modspace: ModspaceType; original_ids: string[] }[];
    unrecognized: string[];
    count: number;
}

export const parseReferences = (text: string, idsModspace: ModspaceType): ParsedReferences => {
    const byModspace = new Map<ModspaceType, Set<string>>();
    const unrecognized: string[] = [];
    for (const input of text.split(/[\s,]+/).filter(Boolean)) {
        const reference = recognize(input, idsModspace);
        if (!reference) {
            unrecognized.push(input);
            continue;
        }
        const ids = byModspace.get(reference.modspace) ?? new Set<string>();
        ids.add(reference.originalId);
        byModspace.set(reference.modspace, ids);
    }
    const items = [...byModspace].map(([modspace, ids]) => ({modspace, original_ids: [...ids]}));
    return {items, unrecognized, count: items.reduce((sum, group) => sum + group.original_ids.length, 0)};
};

const recognize = (input: string, idsModspace: ModspaceType): { modspace: ModspaceType; originalId: string } | null => {
    for (const modspace of externalModspaces) {
        const match = modspace.linkPattern.exec(input);
        if (match) {
            return {modspace: modspace.type, originalId: match[1]};
        }
    }
    const chosen = externalModspaces.find(modspace => modspace.type === idsModspace);
    return chosen?.idPattern.test(input) ? {modspace: chosen.type, originalId: input} : null;
};
