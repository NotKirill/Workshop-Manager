import router from "@/app/routes/router.tsx";
import {RouterProvider} from "react-router";
import {AuthBootstrap} from "@/features/auth/components/AuthBootstrap.tsx";

export const App = () => {
    return (
        <>
            <AuthBootstrap/>
            <RouterProvider router={router}/>
        </>
    );
};