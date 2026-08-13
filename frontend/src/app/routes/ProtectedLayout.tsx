import {Navigate, Outlet} from "react-router";
import {useAppSelector} from "@/app/hooks.ts";

const ProtectedLayout = () => {
    const isAuthenticated = useAppSelector(
        state => state.auth.isAuthenticated
    );

    if (!isAuthenticated) {
        return <Navigate to="/login" replace/>;
    }

    return <Outlet/>;

    // return (
    //     <>
    //         <Navbar />
    //         <Outlet />
    //     </>
    // );
};

export default ProtectedLayout;