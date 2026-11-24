import {toaster} from "@/components/ui/chakra/toaster.tsx";
import {getErrorMessage} from "@/lib/errors.ts";

export const notifySuccess = (title: string, description?: string) => {
    toaster.create({title, description, type: "success", duration: 5000});
};

export const notifyError = (error: unknown, title = "Request failed") => {
    toaster.create({title, description: getErrorMessage(error), type: "error", closable: true, duration: 8000});
};

export const notifyInfo = (title: string, description?: string) => {
    toaster.create({title, description, type: "info"});
};
