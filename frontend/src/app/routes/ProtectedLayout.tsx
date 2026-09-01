import {Navigate, Outlet} from "react-router";
import {useAppSelector} from "@/app/hooks.ts";
import {AuthBootstrap} from "@/features/auth/components/AuthBootstrap.tsx";

const ProtectedLayout = () => {
    const isAuthenticated = useAppSelector(
        state => state.auth.isAuthenticated
    );

    if (!isAuthenticated) {
        return <Navigate to="/login" replace/>;
    }

    return (
        <>
            <AuthBootstrap/>
            <Outlet/>
        </>
    );

    // return (
    //     <>
    //         <Navbar />
    //         <Outlet />
    //     </>
    // );
};

export default ProtectedLayout;