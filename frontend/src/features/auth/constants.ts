import SteamIcon from "@/assets/steam.svg"
import type {AuthProvider} from "@/features/auth/types.ts";

export const providers: AuthProvider[] = [
    {
        id: "steam",
        name: "Steam",
        icon: SteamIcon
    }
];