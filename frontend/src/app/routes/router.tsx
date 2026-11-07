import {createBrowserRouter, Navigate} from "react-router";
import PublicLayout from "@/app/routes/PublicLayout.tsx";
import ProtectedLayout from "@/app/routes/ProtectedLayout.tsx";
import LoginPage from "@/features/auth/components/LoginPage.tsx";
import LoginSuccessCallback from "@/features/auth/components/LoginSuccessCallback.tsx";
import ChangePasswordPage from "@/features/auth/components/ChangePasswordPage.tsx";
import {AppShell} from "@/features/layout/AppShell.tsx";
import DashboardPage from "@/features/shared/DashboardPage.tsx";
import ProfilePage from "@/features/shared/ProfilePage.tsx";

export const router = createBrowserRouter([
    {
        element: <PublicLayout/>,
        children: [
            {path: "/login", element: <LoginPage/>},
            {path: "/login-success", element: <LoginSuccessCallback/>},
            {path: "/change-password", element: <ChangePasswordPage/>},
        ],
    },
    {
        element: <ProtectedLayout/>,
        children: [
            {
                element: <AppShell/>,
                children: [
                    {path: "/dashboard", element: <DashboardPage/>},
                    {path: "/profile", element: <ProfilePage/>},
                    {path: "/", element: <Navigate to="/dashboard" replace/>},
                ],
            },
        ],
    },
    {
        path: "*",
        element: <Navigate to="/" replace/>,
    },
]);

export default router;
