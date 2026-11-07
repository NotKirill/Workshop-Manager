import {Button, Checkbox, Stack, Text} from "@chakra-ui/react";
import {useState} from "react";
import {useReviewDependencies} from "@/features/items/mutations.ts";
import {notifySuccess} from "@/lib/notify.ts";
import type {ItemDetail} from "@/features/items/types.ts";
import {AppDialog} from "@/features/shared/AppDialog.tsx";

export const ReviewDependenciesDialog = ({item, open, onOpenChange}: { item: ItemDetail; open: boolean; onOpenChange: (o: boolean) => void }) => {
    const linked = item.relations.filter(r => r.direction === "OUTGOING" && r.relation === "DEPENDENCY");
    const linkedIds = new Set(linked.map(r => r.item_id));
    const declaredIds = new Set(item.dependencies.flatMap(dep => (dep.personal_id === null ? [] : [dep.personal_id])));
    const declaredNew = item.dependencies.filter(dep => dep.personal_id === null || !linkedIds.has(dep.personal_id));
    const dropped = linked.filter(r => !declaredIds.has(r.item_id));
    // Following the source is preselected
    const [add, setAdd] = useState(() => new Set(declaredNew.map(dep => dep.original_id)));
    const [remove, setRemove] = useState(() => new Set(dropped.map(r => r.item_id)));

    const toggle = <T, >(set: Set<T>, value: T) => {
        const next = new Set(set);
        if (next.has(value)) next.delete(value); else next.add(value);
        return next;
    };
    const review = useReviewDependencies(item.id);

    return (
        <AppDialog open={open} onOpenChange={onOpenChange} title="Review dependencies" lazy footer={<>
            <Button variant="outline" onClick={() => onOpenChange(false)}>Later</Button>
            <Button colorPalette="brand" loading={review.isPending} onClick={() => review.mutate({add: [...add], remove: [...remove]}, {
                onSuccess: () => {
                    notifySuccess("Dependencies reviewed");
                    onOpenChange(false);
                },
            })}>Done</Button>
        </>}>
            <Stack gap="4">
                {declaredNew.length === 0 && dropped.length === 0 && (
                    <Text textStyle="hint">Your dependencies already match the source.</Text>
                )}
                {declaredNew.length > 0 && (
                    <Stack gap="1">
                        <Text textStyle="sectionTitle">Declared by the source, not in your list</Text>
                        {declaredNew.map(dep => (
                            <Checkbox.Root key={dep.original_id} size="sm" checked={add.has(dep.original_id)}
                                           onCheckedChange={() => setAdd(set => toggle(set, dep.original_id))}>
                                <Checkbox.HiddenInput/>
                                <Checkbox.Control/>
                                <Checkbox.Label>Add {dep.title ?? dep.original_id}</Checkbox.Label>
                            </Checkbox.Root>
                        ))}
                    </Stack>
                )}
                {dropped.length > 0 && (
                    <Stack gap="1">
                        <Text textStyle="sectionTitle">In your list, no longer declared by the source</Text>
                        {dropped.map(r => (
                            <Checkbox.Root key={r.item_id} size="sm" checked={remove.has(r.item_id)}
                                           onCheckedChange={() => setRemove(set => toggle(set, r.item_id))}>
                                <Checkbox.HiddenInput/>
                                <Checkbox.Control/>
                                <Checkbox.Label>Remove {r.title ?? `#${r.item_id}`}</Checkbox.Label>
                            </Checkbox.Root>
                        ))}
                        <Text fontSize="xs" color="fg.muted">Removed items stay in your library.</Text>
                    </Stack>
                )}
            </Stack>
        </AppDialog>
    );
};

export default ReviewDependenciesDialog;
