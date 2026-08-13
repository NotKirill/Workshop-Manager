import tokenStorage from "@/app/tokenStorage.ts";
import store from "@/app/store.ts";
import type {AuthResponse} from "@/features/auth/types.ts";
import {setAuthenticated, unsetAuthenticated} from "@/features/auth/slice.ts";


export const authService = {
    saveAuth: (response: AuthResponse) => {
        tokenStorage.setTokens(response.access_token, response.refresh_token);
        store.dispatch(setAuthenticated());
    },
    clearAuth: () => {
        tokenStorage.clear();
        store.dispatch(unsetAuthenticated());
    },
};
export default authService;