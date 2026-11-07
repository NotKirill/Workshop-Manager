import {toaster} from "@/components/ui/chakra/toaster.tsx";
import {getErrorMessage} from "@/lib/errors.ts";

export const notifySuccess = (title: string, description?: string) => {
    toaster.create({title, description, type: "success", closable: true});
};

export const notifyError = (error: unknown, title = "Request failed") => {
    toaster.create({title, description: getErrorMessage(error), type: "error", closable: true});
};

export const notifyInfo = (title: string, description?: string) => {
    toaster.create({title, description, type: "info", closable: true});
};
