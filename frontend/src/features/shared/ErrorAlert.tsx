import {Alert} from "@chakra-ui/react";
import type {ReactNode} from "react";

interface ErrorAlertProps {
    title?: ReactNode;
    description?: ReactNode;
    size?: "sm" | "md" | "lg";
}

export const ErrorAlert = ({title, description, size}: ErrorAlertProps) => (
    <Alert.Root status="error" size={size}>
        <Alert.Indicator/>
        {title && description ? (
            <Alert.Content>
                <Alert.Title>{title}</Alert.Title>
                <Alert.Description>{description}</Alert.Description>
            </Alert.Content>
        ) : title ? (
            <Alert.Title>{title}</Alert.Title>
        ) : (
            <Alert.Description>{description}</Alert.Description>
        )}
    </Alert.Root>
);
