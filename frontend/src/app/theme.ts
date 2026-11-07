import {createSystem, defaultConfig, defineConfig} from "@chakra-ui/react";
import {tableAnatomy} from "@chakra-ui/react/anatomy";

const BRAND = "teal";
const shade = (light: number, dark: number) =>
    ({value: {_light: `{colors.${BRAND}.${light}}`, _dark: `{colors.${BRAND}.${dark}}`}});

const config = defineConfig({
    theme: {
        tokens: {
            sizes: {
                header: {value: "53px"},
            },
        },
        semanticTokens: {
            colors: {
                brand: {
                    contrast: {value: {_light: "white", _dark: "white"}},
                    fg: shade(700, 300),
                    subtle: shade(100, 900),
                    muted: shade(200, 800),
                    emphasized: shade(300, 700),
                    solid: shade(600, 600),
                    focusRing: shade(500, 500),
                    border: shade(500, 400),
                },
            },
        },
        textStyles: {
            hint: {value: {fontSize: "sm", color: "fg.muted"}},
            sectionTitle: {value: {fontSize: "sm", fontWeight: "semibold"}},
        },
        slotRecipes: {
            table: {slots: tableAnatomy.keys(), defaultVariants: {size: "sm", variant: "outline"}},
        },
    },
});

export const system = createSystem(defaultConfig, config);
