import {createBrowserRouter, Navigate} from "react-router";
import PublicLayout from "@/app/routes/PublicLayout.tsx";
import ProtectedLayout from "@/app/routes/ProtectedLayout.tsx";
import LoginPage from "@/features/auth/components/LoginPage.tsx";
import LoginSuccessCallback from "@/features/auth/components/LoginSuccessCallback.tsx";
import ChangePasswordPage from "@/features/auth/components/ChangePasswordPage.tsx";
import {AppShell} from "@/features/layout/AppShell.tsx";
import {LibraryPage} from "@/features/items/components/LibraryPage.tsx";
import {ItemPage} from "@/features/items/components/ItemPage.tsx";
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
                    {path: "/library", element: <LibraryPage/>},
                    {path: "/items/:id", element: <ItemPage/>},
                    {path: "/profile", element: <ProfilePage/>},
                    {path: "/dashboard", element: <Navigate to="/library" replace/>},
                    {path: "/", element: <Navigate to="/library" replace/>},
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
