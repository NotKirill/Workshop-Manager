import {Navigate, Outlet, useLocation} from "react-router";
import {useAppSelector} from "@/app/hooks.ts";

const ProtectedLayout = () => {
    const isAuthenticated = useAppSelector(state => state.auth.isAuthenticated);
    const location = useLocation();

    if (!isAuthenticated) {
        return <Navigate to="/login" replace state={{from: location.pathname + location.search}}/>;
    }
    return <Outlet/>;
};

export default ProtectedLayout;
