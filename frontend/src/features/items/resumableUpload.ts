import axios from "axios";
import itemsApi from "@/features/items/api.ts";
import type {ApiError} from "@/lib/apiTypes.ts";
import type {ContentRevision, ContentUpload} from "@/features/items/types.ts";

const MAX_ATTEMPTS = 6;

const storageKey = (itemId: number, file: File) =>
    `content-upload:${itemId}:${file.name}:${file.size}:${file.lastModified}`;

// Storage may be unavailable (private mode, blocked site data): the upload then just can't resume after a reload
const readUploadId = (key: string): number | null => {
    try {
        const value = localStorage.getItem(key);
        return value === null ? null : Number(value);
    } catch {
        return null;
    }
};

const writeUploadId = (key: string, uploadId: number | null) => {
    try {
        if (uploadId === null) localStorage.removeItem(key);
        else localStorage.setItem(key, String(uploadId));
    } catch {
        // see readUploadId
    }
};

const errorCode = (error: unknown) =>
    axios.isAxiosError<ApiError>(error) ? error.response?.data?.code : undefined;

const isTemporary = (error: unknown) => {
    if (!axios.isAxiosError(error)) return false;
    const status = error.response?.status;
    return status === undefined || status >= 500 || status === 429 || errorCode(error) === "UPLOAD_BUSY";
};

const sleep = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

const startOrResume = async (itemId: number, file: File, key: string): Promise<ContentUpload> => {
    const uploadId = readUploadId(key);
    if (uploadId !== null) {
        try {
            return await itemsApi.contentUpload(itemId, uploadId);
        } catch (error) {
            if (errorCode(error) !== "UPLOAD_NOT_FOUND") throw error;
        }
    }
    const upload = await itemsApi.startContentUpload(itemId, file.name, file.size);
    writeUploadId(key, upload.upload_id);
    return upload;
};

export const uploadResumable = async (itemId: number, file: File,
                                      onProgress: (percent: number) => void): Promise<ContentRevision> => {
    const key = storageKey(itemId, file);
    let upload = await startOrResume(itemId, file, key);
    const report = (bytes: number) => onProgress(Math.floor((bytes / upload.size) * 100));
    let failures = 0;
    let resync = false;

    for (;;) {
        try {
            if (resync) {
                upload = await itemsApi.contentUpload(itemId, upload.upload_id);
                resync = false;
            }
            const offset = upload.received;
            report(offset);
            // At the end the part is empty: that stores the file again if a previous attempt failed
            const part = file.slice(offset, Math.min(offset + upload.chunk_size, upload.size));
            upload = await itemsApi.uploadContentPart(itemId, upload.upload_id, offset, part,
                loaded => report(offset + loaded));
            failures = 0;
            if (upload.revision) {
                writeUploadId(key, null);
                onProgress(100);
                return upload.revision;
            }
        } catch (error) {
            if (errorCode(error) === "UPLOAD_OFFSET_MISMATCH") {
                resync = true;
                continue;
            }
            failures++;
            if (!isTemporary(error) || failures >= MAX_ATTEMPTS) throw error;
            await sleep(1000 * 2 ** (failures - 1));
            resync = true;
        }
    }
};
