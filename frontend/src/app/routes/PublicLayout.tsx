import {Navigate, Outlet, useLocation} from "react-router";
import {useAppSelector} from "@/app/hooks.ts";

const returnPath = (state: unknown): string => {
    const from = (state as { from?: unknown } | null)?.from;
    return typeof from === "string" && from.startsWith("/") && !from.startsWith("//") ? from : "/dashboard";
};

const PublicLayout = () => {
    const isAuthenticated = useAppSelector(
        state => state.auth.isAuthenticated
    );
    const location = useLocation();

    if (isAuthenticated) {
        return <Navigate to={returnPath(location.state)} replace/>;
    }

    return <Outlet/>;
};

export default PublicLayout;
