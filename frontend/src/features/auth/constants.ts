import SteamIcon from "@/assets/steam.svg"
import type {AuthProvider} from "@/features/auth/types.ts";
import {useSteamLogin} from "@/features/auth/mutations.ts";

export const providers: AuthProvider[] = [
    {
        id: "steam",
        name: "Steam",
        icon: SteamIcon,
        redirectHook: useSteamLogin
    }
];