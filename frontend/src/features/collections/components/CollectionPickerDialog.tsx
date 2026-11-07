import {Button, HStack, Input, NativeSelect, Stack, Text} from "@chakra-ui/react";
import {useState} from "react";
import {useCollections} from "@/features/collections/queries.ts";
import {useChangeCollectionItems} from "@/features/collections/mutations.ts";
import type {CollectionKind} from "@/features/collections/types.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {AppDialog} from "@/features/shared/AppDialog.tsx";
import {Field} from "@/features/shared/Field.tsx";

interface CollectionPickerDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    kind: CollectionKind;
    gameId: number | null;
    itemIds: number[];
    mode: "add" | "remove";
}

export const CollectionPickerDialog = ({open, onOpenChange, kind, gameId, itemIds, mode}: CollectionPickerDialogProps) => {
    const [selected, setSelected] = useState("");
    const [newName, setNewName] = useState("");
    const label = kind === "tags" ? "tag" : "group";

    const {data: collections} = useCollections(kind, gameId, open);
    const mutation = useChangeCollectionItems(kind);
    const submit = () => {
        if (gameId === null) return;
        mutation.mutate({
            target: selected === "new" ? {name: newName.trim(), gameId} : Number(selected),
            itemIds,
            mode,
        }, {
            onSuccess: result => {
                notifySuccess(`${result.affected} item(s) ${mode === "add" ? "added" : "removed"}`);
                onOpenChange(false);
            },
        });
    };

    const valid = selected !== "" && (selected !== "new" || newName.trim().length > 0);
    return (
        <AppDialog open={open} onOpenChange={onOpenChange} title={mode === "add" ? `Add to ${label}` : `Remove from ${label}`} lazy footer={<>
            <HStack>
                <Button variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
                <Button colorPalette="brand" disabled={!valid || gameId === null}
                        loading={mutation.isPending} onClick={submit}>
                    {mode === "add" ? "Add" : "Remove"}
                </Button>
            </HStack>
        </>}>
            {gameId === null ? (
                <Text>Select items of a single game: {label}s belong to one game.</Text>
            ) : (
                <Stack gap="3">
                    <Field label={label[0].toUpperCase() + label.slice(1)}>
                        <NativeSelect.Root>
                            <NativeSelect.Field value={selected}
                                                onChange={event => setSelected(event.target.value)}>
                                <option value="">Choose…</option>
                                {collections?.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                                {mode === "add" && <option value="new">+ New {label}…</option>}
                            </NativeSelect.Field>
                            <NativeSelect.Indicator/>
                        </NativeSelect.Root>
                    </Field>
                    {selected === "new" && (
                        <Field label="Name">
                            <Input value={newName} onChange={event => setNewName(event.target.value)}/>
                        </Field>
                    )}
                    <Text textStyle="hint">{itemIds.length} item(s) selected.</Text>
                </Stack>
            )}
        </AppDialog>
    );
};

export default CollectionPickerDialog;
