import {useMeRequest} from "@/features/auth/mutations.ts";
import {useEffect} from "react";

export const AuthBootstrap = () => {
    const {mutate} = useMeRequest();

    useEffect(() => {
        mutate();
    }, [mutate]);

    return null;
};