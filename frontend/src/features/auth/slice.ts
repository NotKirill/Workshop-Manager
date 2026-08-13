import {createSlice} from "@reduxjs/toolkit";
import tokenStorage from "@/app/tokenStorage.ts";

interface AuthState {
    // user: User | null;
    isAuthenticated: boolean;
}

const initialState: AuthState = {
    isAuthenticated: !!tokenStorage.getAccessToken(),
};

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setAuthenticated: (state) => {
            state.isAuthenticated = true;
        },
        unsetAuthenticated: (state) => {
            state.isAuthenticated = false;
        },
    },
});

export const {setAuthenticated, unsetAuthenticated} = authSlice.actions;

export default authSlice.reducer;