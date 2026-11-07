import {Button, chakra, Input, NativeSelect, Stack, Text} from "@chakra-ui/react";
import {useState} from "react";
import {useRelationCandidates} from "@/features/items/queries.ts";
import {useAddRelation} from "@/features/items/mutations.ts";
import {notifySuccess} from "@/lib/notify.ts";
import type {ItemDetail, RelationType} from "@/features/items/types.ts";
import {AppDialog} from "@/features/shared/AppDialog.tsx";
import {Field} from "@/features/shared/Field.tsx";

export const AddRelationDialog = ({item, open, onOpenChange}: { item: ItemDetail; open: boolean; onOpenChange: (o: boolean) => void }) => {
    const [search, setSearch] = useState("");
    const [targetId, setTargetId] = useState("");
    const [relation, setRelation] = useState<RelationType>("DEPENDENCY");
    const {data} = useRelationCandidates(search, open);
    const add = useAddRelation(item.id);
    return (
        <AppDialog open={open} onOpenChange={onOpenChange} title="Add relation" lazy footer={<>
            <Button variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button colorPalette="brand" disabled={!targetId} loading={add.isPending}
                    onClick={() => add.mutate({targetId: Number(targetId), relation}, {
                        onSuccess: () => {
                            notifySuccess("Relation added");
                            onOpenChange(false);
                        },
                    })}>Add</Button>
        </>}>
            <Stack gap="3">
                <Field label="This item…">
                    <NativeSelect.Root>
                        <NativeSelect.Field value={relation}
                                            onChange={event => setRelation(event.target.value as RelationType)}>
                            <option value="DEPENDENCY">requires</option>
                            <option value="PARENT">is the parent of</option>
                            <option value="ALTERNATE">is an alternative to</option>
                        </NativeSelect.Field>
                        <NativeSelect.Indicator/>
                    </NativeSelect.Root>
                </Field>
                <Field label="Find item">
                    <Input value={search} placeholder="Search your library"
                           onChange={event => setSearch(event.target.value)}/>
                </Field>
                <Stack gap="1" maxH="56" overflowY="auto">
                    {data?.items.filter(candidate => candidate.id !== item.id).map(candidate => (
                        <chakra.button type="button" key={candidate.id} display="block" w="full" textAlign="left"
                                       p="2" borderRadius="md" cursor="pointer"
                                       bg={String(candidate.id) === targetId ? "brand.subtle" : undefined}
                                       aria-pressed={String(candidate.id) === targetId}
                                       _hover={{bg: "bg.muted"}} onClick={() => setTargetId(String(candidate.id))}>
                            <Text as="span" display="block" fontSize="sm">{candidate.title ?? candidate.original_id}</Text>
                            <Text as="span" display="block" fontSize="xs" color="fg.muted">{candidate.game.name}</Text>
                        </chakra.button>
                    ))}
                </Stack>
            </Stack>
        </AppDialog>
    );
};

export default AddRelationDialog;
