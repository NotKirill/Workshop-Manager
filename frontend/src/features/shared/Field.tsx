import {Field as ChakraField} from "@chakra-ui/react";
import type {ReactNode} from "react";

export interface FieldProps extends Omit<ChakraField.RootProps, "label"> {
    label?: ReactNode;
    helperText?: ReactNode;
    errorText?: ReactNode;
}

export const Field = ({label, helperText, errorText, children, ...rest}: FieldProps) => (
    <ChakraField.Root {...rest}>
        {label && <ChakraField.Label>{label}</ChakraField.Label>}
        {children}
        {helperText && <ChakraField.HelperText>{helperText}</ChakraField.HelperText>}
        {errorText && <ChakraField.ErrorText overflowWrap="anywhere">{errorText}</ChakraField.ErrorText>}
    </ChakraField.Root>
);
