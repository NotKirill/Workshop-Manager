import {Center, Spinner, Text, VStack} from "@chakra-ui/react";
import {useEffect, useRef} from "react";
import {useNavigate, useSearchParams} from "react-router";
import {useLoginSuccess} from "@/features/auth/mutations.ts";
import {getErrorCode} from "@/lib/errors.ts";
import steamOpenIdService from "@/features/auth/providers/steam/service.ts";

function LoginSuccessCallback() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const {mutate: loginSuccess} = useLoginSuccess();
    const consumed = useRef(false);

    useEffect(() => {
        if (consumed.current) {
            return;
        }
        const token = searchParams.get("ott");
        if (!token) {
            void navigate("/login?error=missing_token", {replace: true});
            return;
        }
        if (!steamOpenIdService.consumeLoginState(searchParams.get("state"))) {
            void navigate("/login?error=foreign_flow", {replace: true});
            return;
        }
        consumed.current = true;
        loginSuccess({ott: token}, {
            onError: error => {
                const code = getErrorCode(error);
                const reason = code === "ACCOUNT_NOT_LINKED" ? "not_linked"
                    : code === "ACCOUNT_DISABLED" || code === "ACCOUNT_LOCKED" ? "disabled" : "steam_failed";
                void navigate(`/login?error=${reason}`, {replace: true});
            },
        });
    }, [navigate, searchParams, loginSuccess]);

    return (
        <Center h="100vh">
            <VStack><Spinner/><Text>Signing you in…</Text></VStack>
        </Center>
    );
}

export default LoginSuccessCallback;
