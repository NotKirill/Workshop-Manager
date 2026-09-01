import router from "@/app/routes/router.tsx";
import {RouterProvider} from "react-router";

export const App = () => {
    return (
        <RouterProvider router={router}/>
    );
};