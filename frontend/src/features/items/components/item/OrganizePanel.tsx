import {Badge, Box, Button, Checkbox, HStack, Input, Popover, Portal, Stack, Text, Wrap} from "@chakra-ui/react";
import {useState} from "react";
import {FiEdit2} from "react-icons/fi";
import {useRateItem, useSetItemCollections} from "@/features/items/mutations.ts";
import {useCollections} from "@/features/collections/queries.ts";
import {useCreateCollection} from "@/features/collections/mutations.ts";
import type {CollectionKind} from "@/features/collections/types.ts";
import {RatingStars} from "@/features/shared/RatingStars.tsx";
import {usePermissions} from "@/features/auth/queries.ts";
import {UNKNOWN_GAME_ID} from "@/features/games/queries.ts";
import type {NamedRef} from "@/lib/apiTypes.ts";
import type {ItemDetail} from "@/features/items/types.ts";

const CollectionEditor = ({item, kind, current}: { item: ItemDetail; kind: CollectionKind; current: NamedRef[] }) => {
    const canWrite = usePermissions()("ITEMS_WRITE");
    const [newName, setNewName] = useState("");
    const label = kind === "tags" ? "tag" : "group";
    const {data: available} = useCollections(kind, item.game.id);
    const currentIds = new Set(current.map(c => c.id));

    const save = useSetItemCollections(item.id, kind);
    const create = useCreateCollection(kind);
    const submit = () => create.mutate({name: newName.trim(), gameId: item.game.id}, {
        onSuccess: created => {
            setNewName("");
            save.mutate([...currentIds, created.id]);
        },
    });

    const toggle = (id: number) => {
        const next = new Set(currentIds);
        if (next.has(id)) next.delete(id); else next.add(id);
        save.mutate([...next]);
    };

    return (
        <HStack align="start" gap="2" wrap="wrap">
            {current.length === 0 && <Text textStyle="hint">No {label}s</Text>}
            {current.map(c => (
                <Badge key={c.id} colorPalette={kind === "tags" ? "purple" : "cyan"} variant="surface">{c.name}</Badge>
            ))}
            {canWrite && <Popover.Root positioning={{placement: "bottom-start"}}>
                <Popover.Trigger asChild>
                    <Button size="2xs" variant="ghost"><FiEdit2/> Edit</Button>
                </Popover.Trigger>
                <Portal>
                    <Popover.Positioner>
                        <Popover.Content w="64">
                            <Popover.Body>
                                <Stack gap="2">
                                    <Text fontSize="xs" color="fg.muted">{label}s of {item.game.name}</Text>
                                    <Stack gap="1" maxH="48" overflowY="auto">
                                        {available?.map(c => (
                                            <Checkbox.Root key={c.id} size="sm" checked={currentIds.has(c.id)}
                                                           disabled={save.isPending}
                                                           onCheckedChange={() => toggle(c.id)}>
                                                <Checkbox.HiddenInput/>
                                                <Checkbox.Control/>
                                                <Checkbox.Label>{c.name}</Checkbox.Label>
                                            </Checkbox.Root>
                                        ))}
                                    </Stack>
                                    <HStack>
                                        <Input size="xs" placeholder={`New ${label}`} value={newName}
                                               onChange={event => setNewName(event.target.value)}
                                               onKeyDown={event => {
                                                   if (event.key === "Enter" && newName.trim()) submit();
                                               }}/>
                                        <Button size="xs" disabled={!newName.trim()} loading={create.isPending}
                                                onClick={submit}>Add</Button>
                                    </HStack>
                                </Stack>
                            </Popover.Body>
                        </Popover.Content>
                    </Popover.Positioner>
                </Portal>
            </Popover.Root>}
        </HStack>
    );
};

export const OrganizePanel = ({item}: { item: ItemDetail }) => {
    const canWrite = usePermissions()("ITEMS_WRITE");
    const rate = useRateItem(item.id);
    return (
        <Stack gap="3">
            <Box>
                <Text textStyle="sectionTitle" mb="1">Your rating</Text>
                <RatingStars value={item.rating} size={18}
                             onChange={canWrite ? score => rate.mutate(score) : undefined}/>
            </Box>
            {item.game.id === UNKNOWN_GAME_ID ? (
                <Text textStyle="hint">
                    Tags and groups belong to a game, so they become available once this item's game is known.
                </Text>
            ) : (
                <>
                    <Box>
                        <Text textStyle="sectionTitle" mb="1">Tags</Text>
                        <CollectionEditor item={item} kind="tags" current={item.tags}/>
                    </Box>
                    <Box>
                        <Text textStyle="sectionTitle" mb="1">Groups</Text>
                        <CollectionEditor item={item} kind="groups" current={item.groups}/>
                    </Box>
                </>
            )}
            {item.steam && item.steam.tags.length > 0 && (
                <Box>
                    <Text textStyle="sectionTitle" mb="1">Steam tags</Text>
                    <Wrap gap="1">
                        {item.steam.tags.map(tag => <Badge key={tag} variant="outline">{tag}</Badge>)}
                    </Wrap>
                </Box>
            )}
        </Stack>
    );
};

export default OrganizePanel;
