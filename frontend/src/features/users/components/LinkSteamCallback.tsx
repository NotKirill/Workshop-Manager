import {Center, Spinner, Text, VStack} from "@chakra-ui/react";
import {useEffect, useRef} from "react";
import {useNavigate, useSearchParams} from "react-router";
import {useFinishSteamLink} from "@/features/users/mutations.ts";
import {notifyError, notifySuccess} from "@/lib/notify.ts";

export const LinkSteamCallback = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const {mutate: finishSteamLink} = useFinishSteamLink();
    const consumed = useRef(false);

    useEffect(() => {
        if (consumed.current) return;
        consumed.current = true;
        const ott = searchParams.get("ott");
        if (!ott) {
            void navigate("/profile?error=steam_failed", {replace: true});
            return;
        }
        finishSteamLink(ott, {
            onSuccess: () => notifySuccess("Steam account linked"),
            onError: error => notifyError(error, "Could not link Steam account"),
            onSettled: () => void navigate("/profile", {replace: true}),
        });
    }, [finishSteamLink, navigate, searchParams]);

    return (
        <Center py="24">
            <VStack><Spinner/><Text>Linking your Steam account…</Text></VStack>
        </Center>
    );
};

export default LinkSteamCallback;
