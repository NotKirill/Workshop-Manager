import {Navigate, Outlet} from "react-router";
import {useAppSelector} from "@/app/hooks.ts";

const PublicLayout = () => {
    const isAuthenticated = useAppSelector(
        state => state.auth.isAuthenticated
    );

    if (isAuthenticated) {
        return <Navigate to="/dashboard" replace/>;
    }

    return <Outlet/>;
};

export default PublicLayout;