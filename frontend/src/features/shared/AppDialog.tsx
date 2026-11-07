import {CloseButton, Dialog, Portal} from "@chakra-ui/react";
import type {ReactNode} from "react";

interface AppDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    title: ReactNode;
    children: ReactNode;
    footer?: ReactNode;
    size?: "xs" | "sm" | "md" | "lg" | "xl";
    lazy?: boolean;
    closeButton?: boolean;
    role?: "dialog" | "alertdialog";
}

export const AppDialog = ({
                              open, onOpenChange, title, children, footer, size, lazy = false, closeButton = false,
                              role,
                          }: AppDialogProps) => (
    <Dialog.Root open={open} onOpenChange={event => onOpenChange(event.open)} size={size} lazyMount={lazy}
                 unmountOnExit={lazy} role={role}>
        <Portal>
            <Dialog.Backdrop/>
            <Dialog.Positioner>
                <Dialog.Content>
                    <Dialog.Header>
                        <Dialog.Title>{title}</Dialog.Title>
                    </Dialog.Header>
                    <Dialog.Body>{children}</Dialog.Body>
                    {footer && <Dialog.Footer>{footer}</Dialog.Footer>}
                    {closeButton && (
                        <Dialog.CloseTrigger asChild>
                            <CloseButton size="sm"/>
                        </Dialog.CloseTrigger>
                    )}
                </Dialog.Content>
            </Dialog.Positioner>
        </Portal>
    </Dialog.Root>
);
