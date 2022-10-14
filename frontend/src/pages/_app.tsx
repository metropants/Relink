import type {AppProps} from 'next/app'
import {MantineProvider} from "@mantine/core";
import AppHeader from "../components/AppHeader";

function MyApp({Component, pageProps}: AppProps) {
    return (
        <MantineProvider withGlobalStyles withNormalizeCSS theme={{
            colorScheme: "dark",
            focusRing: "never"
        }}>
            <AppHeader/>
            <Component {...pageProps} />
        </MantineProvider>
    );
}

export default MyApp
