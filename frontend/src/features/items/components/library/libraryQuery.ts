import {quoteSearchValue} from "@/lib/search.ts";

export interface Filters {
    game: string;
    tag: string;
    group: string;
    modspace: string;
    autoBackup: string;
    status: string;
    backup: string;
    sort: string;
}

export const emptyFilters: Filters = {
    game: "", tag: "", group: "", modspace: "", autoBackup: "", status: "", backup: "", sort: "",
};

export const buildQuery = (text: string, filters: Filters): string => {
    const parts: string[] = [];
    const trimmed = text.trim();
    const hasDirective = /\b(order|size):/i.test(trimmed);
    if (trimmed) {
        // A top-level OR would otherwise swallow the filters into its last branch
        parts.push(/\bOR\b|\|\|/.test(trimmed) && !hasDirective ? `(${trimmed})` : trimmed);
    }
    if (filters.game) parts.push(`game:${quoteSearchValue(filters.game)}`);
    if (filters.tag) parts.push(`tag:${quoteSearchValue(filters.tag)}`);
    if (filters.group) parts.push(`group:${quoteSearchValue(filters.group)}`);
    if (filters.modspace) parts.push(`modspace:${filters.modspace}`);
    if (filters.autoBackup) parts.push(`auto_backup:${filters.autoBackup}`);
    if (filters.status) parts.push(`status:${filters.status}`);
    if (filters.backup) parts.push(`backup:${filters.backup}`);
    if (filters.sort && !hasDirective) parts.push(`order:${filters.sort}`);
    return parts.join(" ");
};
