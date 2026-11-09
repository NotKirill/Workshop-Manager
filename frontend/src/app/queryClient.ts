import { QueryClient } from "@tanstack/react-query";
import axios from "axios";

const retryOnlyTransient = (failureCount: number, error: unknown): boolean => {
    const status = axios.isAxiosError(error) ? error.response?.status : undefined;
    if (status !== undefined && status >= 400 && status < 500) {
        return false;
    }
    return failureCount < 3;
};

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {retry: retryOnlyTransient},
    },
});

export default queryClient;
