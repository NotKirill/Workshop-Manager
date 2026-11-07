import axios from "axios";
import type {ApiError} from "@/lib/apiTypes.ts";

// Long TODO: remove hardcoded concrete meanings, like 'Steam' for generic codes
const CODE_MESSAGES: Record<string, string> = {
    // Authentication and accounts
    INVALID_CREDENTIALS: "Wrong login or password",
    ACCOUNT_DISABLED: "This account is disabled",
    ACCOUNT_LOCKED: "This account is locked",
    ACCOUNT_NOT_LINKED: "This Steam account is not linked to any user. Ask an administrator for access.",
    INVALID_PASSWORD: "The current password is wrong",
    PASSWORD_REQUIRED: "A password is required for username or email sign-in",
    IDENTITY_REQUIRED: "A username, an email or a Steam id is required",
    IDENTITY_TAKEN: "This sign-in is already used by another account",
    LAST_IDENTITY: "The account would have no way to sign in",
    INVALID_STEAM_ID: "A Steam id must be a 17-digit SteamID64",
    INVALID_ONE_TIME_TOKEN: "The sign-in link expired, please try again",
    STEAM_LINK_FOREIGN: "This Steam link was started by another user",
    SELF_LOCKOUT: "You cannot lock yourself out",
    SELF_DELETE: "You cannot delete your own account",
    INVALID_EMAIL: "Enter a valid email address (3-254 characters)",
    INVALID_USERNAME: "A username has 3-50 letters, digits or single hyphens, not at the start or end",
    WEAK_PASSWORD: "8-128 characters with upper and lower case letters, a number and a special character",
    INVALID_PASSWORD_FORMAT: "A password has 8-128 characters",
    USER_NOT_FOUND: "User not found",
    IDENTITY_NOT_FOUND: "This sign-in method isn't set up for the account",
    RATE_LIMIT_EXCEEDED: "Too many attempts, please wait a moment",

    // Library
    ITEM_NOT_FOUND: "Item not found",
    ITEM_BEING_DELETED: "This item is being cleaned up right now, please retry in a minute",
    TITLE_REQUIRED: "Local items need a title",
    TAG_EXISTS: "A tag with this name already exists",
    GROUP_EXISTS: "A group with this name already exists",
    TAG_GAME_MISMATCH: "The tag belongs to another game",
    GROUP_GAME_MISMATCH: "The group belongs to another game",
    INVALID_RELATION: "This relation is not possible",

    // Content and media
    CONTENT_IMPORT_FORBIDDEN: "You are not allowed to store content (backups)",
    CONTENT_SHARED: "Other users rely on these backups; only content managers can delete them",
    BACKUP_NOT_SUPPORTED: "Local items are backed up by uploading files",
    EMPTY_UPLOAD: "The file is empty",
    UPLOAD_TOO_LARGE: "The file is too large",
    UNSUPPORTED_MEDIA: "Only PNG, JPEG, GIF, WebP and AVIF images are allowed",
    UNSUPPORTED_EMBED: "Only YouTube and Sketchfab links are supported",
    INVALID_MEDIA_PURPOSE: "Unknown media purpose",
    MEDIA_NOT_EDITABLE: "This media can't be changed this way",
    LINK_EXPIRED: "The link expired, please reload the page",
    UPLOAD_NOT_FOUND: "The upload expired, please start it again",

    // External services
    STEAM_API_ERROR: "Steam did not answer, please try again later",
    THIRD_PARTY_RATE_LIMIT: "The Steam request quota is used up, please try again later",
};

const SEARCH_CODES = /^(INVALID_SEARCH|INVALID_(ENUM|NUMERIC|DATE|BOOLEAN)_VALUE|UNSORTABLE|UNSUPPORTED_SEARCH|INVALID_SORT)/;

const STATUS_MESSAGES: Record<number, string> = {
    400: "The request is invalid",
    401: "Please sign in again",
    403: "You are not allowed to do this",
    404: "Not found",
    409: "The data was changed at the same time, please retry",
    413: "The file is too large",
    429: "Too many requests, please wait a moment",
    502: "An external service failed, please try again later",
    503: "The service is temporarily unavailable",
};

const FIELD_CONSTRAINTS: Record<string, string> = {
    NotBlank: "is required",
    NotNull: "is required",
    NotEmpty: "is required",
    Size: "has a wrong length",
    Min: "is too small",
    Max: "is too large",
    Pattern: "has a wrong format",
    Email: "is not an email",
    Positive: "must be positive",
};

const getApiError = (error: unknown): ApiError | undefined =>
    axios.isAxiosError<ApiError>(error) ? error.response?.data : undefined;

export const getErrorMessage = (error: unknown, fallback = "Something went wrong"): string => {
    if (axios.isAxiosError<ApiError>(error)) {
        if (!error.response) return "Server is not reachable";
        const data = error.response.data;
        const status = error.response.status;
        if (data?.field_errors && Object.keys(data.field_errors).length > 0) {
            return Object.entries(data.field_errors)
                .map(([field, constraint]) => `${field} ${FIELD_CONSTRAINTS[constraint] ?? "is invalid"}`)
                .join("; ");
        }
        if (data?.code && CODE_MESSAGES[data.code]) return CODE_MESSAGES[data.code];
        if (data?.code && SEARCH_CODES.test(data.code)) return "The search query is invalid, see the search help";
        const text = STATUS_MESSAGES[status] ?? fallback;
        return status >= 500 && data?.error_uuid ? `${text} (error id ${data.error_uuid})` : text;
    }
    if (error instanceof Error && error.message) return error.message;
    return fallback;
};

export const getErrorCode = (error: unknown): string | undefined => getApiError(error)?.code;

export const getCodeMessage = (code: string): string | undefined => CODE_MESSAGES[code];
