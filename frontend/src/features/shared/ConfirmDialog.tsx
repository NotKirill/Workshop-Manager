import {Button, Text} from "@chakra-ui/react";
import type {ReactNode} from "react";
import {AppDialog} from "@/features/shared/AppDialog.tsx";

interface ConfirmDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    title: string;
    children: ReactNode;
    confirmLabel?: string;
    destructive?: boolean;
    loading?: boolean;
    onConfirm: () => void;
}

export const ConfirmDialog = ({
                                  open, onOpenChange, title, children, confirmLabel = "Confirm", destructive = false,
                                  loading = false, onConfirm,
                              }: ConfirmDialogProps) => (
    <AppDialog open={open} onOpenChange={onOpenChange} title={title} role="alertdialog" lazy footer={<>
        <Button variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
        <Button colorPalette={destructive ? "red" : "brand"} loading={loading} onClick={onConfirm}>
            {confirmLabel}
        </Button>
    </>}>
        {typeof children === "string" ? <Text>{children}</Text> : children}
    </AppDialog>
);

export default ConfirmDialog;
