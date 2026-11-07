import router from "@/app/routes/router.tsx";
import {RouterProvider} from "react-router";
import {Toaster} from "@/components/ui/chakra/toaster.tsx";

export const App = () => {
    return (
        <>
            <RouterProvider router={router}/>
            <Toaster/>
        </>
    );
};
