import {Button, HStack, Input, Stack, Text, Textarea} from "@chakra-ui/react";
import {useState} from "react";
import {useUpdateItem} from "@/features/items/mutations.ts";
import {AutoBackupCheckbox} from "@/features/items/components/AutoBackupCheckbox.tsx";
import {notifySuccess} from "@/lib/notify.ts";
import type {ItemDetail} from "@/features/items/types.ts";
import {AppDialog} from "@/features/shared/AppDialog.tsx";
import {Field} from "@/features/shared/Field.tsx";

interface ItemEditDialogProps {
    item: ItemDetail;
    open: boolean;
    onOpenChange: (open: boolean) => void;
}

export const ItemEditDialog = ({item, open, onOpenChange}: ItemEditDialogProps) => {
    const isLocal = item.modspace === "LOCAL";
    const [title, setTitle] = useState(item.personal.title ?? "");
    const [description, setDescription] = useState(item.personal.description ?? "");
    const [commentary, setCommentary] = useState(item.personal.user_commentary ?? "");
    const [autoBackup, setAutoBackup] = useState(item.personal.auto_backup);

    const mutation = useUpdateItem(item.id);
    const save = () => mutation.mutate({
        title: title.trim() || null,
        description: description.trim() || null,
        user_commentary: commentary.trim() || null,
        auto_backup: autoBackup,
    }, {
        onSuccess: () => {
            notifySuccess("Saved");
            onOpenChange(false);
        },
    });

    return (
        <AppDialog open={open} onOpenChange={onOpenChange} title="Edit item" size="lg" closeButton footer={<>
            <HStack>
                <Button variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
                <Button colorPalette="brand" loading={mutation.isPending}
                        disabled={isLocal && title.trim().length === 0}
                        onClick={save}>
                    Save
                </Button>
            </HStack>
        </>}>
            <Stack gap="4">
                {!isLocal && (
                    <Text textStyle="hint">
                        Leave title or description empty to show the original from the modspace.
                        Your values replace the original only for you.
                    </Text>
                )}
                <Field required={isLocal} label="Title">
                    <Input value={title} onChange={event => setTitle(event.target.value)}
                           placeholder={item.original?.title ?? ""}/>
                </Field>
                <Field label="Description" helperText="Steam BBCode is supported ([b], [url=…], [list]…).">
                    <Textarea rows={6} value={description}
                              onChange={event => setDescription(event.target.value)}
                              placeholder={isLocal ? "" : "Original description is used"}/>
                </Field>
                <Field label="Your notes">
                    <Textarea rows={3} value={commentary}
                              onChange={event => setCommentary(event.target.value)}
                              placeholder="Shown next to the description, only for you"/>
                </Field>
                {!isLocal && <AutoBackupCheckbox checked={autoBackup} onChange={setAutoBackup}/>}
            </Stack>
        </AppDialog>
    );
};

export default ItemEditDialog;
