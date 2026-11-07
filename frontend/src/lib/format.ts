import type {ModspaceType} from "@/lib/apiTypes.ts";

export const formatBytes = (bytes: number | null | undefined): string => {
    if (bytes === null || bytes === undefined) return "—";
    if (bytes < 1024) return `${bytes} B`;
    const units = ["KB", "MB", "GB", "TB"];
    let value = bytes / 1024;
    let unit = 0;
    while (value >= 1024 && unit < units.length - 1) {
        value /= 1024;
        unit++;
    }
    return `${value.toFixed(value >= 100 ? 0 : 1)} ${units[unit]}`;
};

export const formatDate = (iso: string | null | undefined): string => {
    if (!iso) return "—";
    const date = new Date(iso);
    return Number.isNaN(date.getTime()) ? "—" : date.toLocaleString();
};

export const formatShortDate = (iso: string | null | undefined): string => {
    if (!iso) return "—";
    const date = new Date(iso);
    return Number.isNaN(date.getTime()) ? "—" : date.toLocaleDateString();
};

export const formatRelative = (iso: string | null | undefined): string => {
    if (!iso) return "—";
    const diff = Date.now() - new Date(iso).getTime();
    const minutes = Math.round(diff / 60000);
    if (Math.abs(minutes) < 1) return "just now";
    if (Math.abs(minutes) < 60) return `${minutes} min ago`;
    const hours = Math.round(minutes / 60);
    if (Math.abs(hours) < 24) return `${hours} h ago`;
    const days = Math.round(hours / 24);
    if (Math.abs(days) < 30) return `${days} d ago`;
    return formatShortDate(iso);
};

export const formatNumber = (value: number | null | undefined): string =>
    value === null || value === undefined ? "—" : value.toLocaleString();

export const modspaceLabels: Record<ModspaceType, string> = {
    STEAM: "Steam Workshop",
    LOCAL: "Local",
    UNKNOWN: "Unknown",
};
