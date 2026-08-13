//import './index.css'

import {Provider as ReduxProvider} from "react-redux";
import {QueryClientProvider} from "@tanstack/react-query";
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import {Provider as ChakraProvider} from "@/components/ui/chakra/provider";
import store from "@/app/store.ts";
import queryClient from "@/app/queryClient.ts";
import {App} from "@/App.tsx";


const root = createRoot(document.getElementById("root")!);

root.render(
    <StrictMode>
        <ReduxProvider store={store}>
            <QueryClientProvider client={queryClient}>
                <ChakraProvider>
                    <App/>
                </ChakraProvider>
            </QueryClientProvider>
        </ReduxProvider>
    </StrictMode>
);