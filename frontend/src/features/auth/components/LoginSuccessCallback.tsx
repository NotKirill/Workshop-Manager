import {useEffect, useRef} from "react";
import {useNavigate, useSearchParams} from "react-router";
import {useLoginSuccess} from "@/features/auth/mutations.ts";

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
            // TODO
            void navigate("/login?error=missing_token", {replace: true});
            return;
        }

        consumed.current = true;
        loginSuccess({ott: token});
    }, [navigate, searchParams, loginSuccess]);

    return <div>Logging you in...</div>;
}

export default LoginSuccessCallback;