import {
    Badge,
    Box,
    Button,
    Card,
    Container,
    EmptyState,
    Grid,
    Heading,
    HStack,
    IconButton,
    Image,
    Input,
    Stack,
    Text,
    VStack,
} from "@chakra-ui/react";
import {useState} from "react";
import {Link as RouterLink} from "react-router";
import {FiCheck, FiEdit2, FiFolder, FiSearch, FiTrash2, FiX} from "react-icons/fi";
import {useCollections} from "@/features/collections/queries.ts";
import {useCreateCollection, useDeleteCollection, useRenameCollection} from "@/features/collections/mutations.ts";
import type {CollectionKind} from "@/features/collections/types.ts";
import {UNKNOWN_GAME_ID, useGames} from "@/features/games/queries.ts";
import {ConfirmDialog} from "@/features/shared/ConfirmDialog.tsx";
import {usePermissions} from "@/features/auth/queries.ts";
import type {Collection} from "@/features/collections/types.ts";
import type {Game} from "@/features/games/types.ts";
import {quoteSearchValue} from "@/lib/search.ts";

const CollectionRow = ({kind, collection}: { kind: CollectionKind; collection: Collection }) => {
    const [editing, setEditing] = useState(false);
    const [name, setName] = useState(collection.name);
    const [confirm, setConfirm] = useState(false);
    const canWrite = usePermissions()("ITEMS_WRITE");
    const rename = useRenameCollection(kind, collection.id);
    const remove = useDeleteCollection(kind, collection.id);
    const searchTag = kind === "tags" ? "tag" : "group";
    const canSave = name.trim() !== "" && name.trim() !== collection.name;
    const startEditing = () => {
        setName(collection.name);
        setEditing(true);
    };
    const save = () => rename.mutate(name.trim(), {onSuccess: () => setEditing(false)});
    return (
        <HStack justify="space-between" py="1">
            {editing ? (
                <HStack flex="1">
                    <Input size="sm" value={name} autoFocus onChange={event => setName(event.target.value)}
                           onKeyDown={event => {
                               if (event.key === "Enter" && canSave) save();
                               if (event.key === "Escape") setEditing(false);
                           }}/>
                    <IconButton aria-label="Save" size="xs" disabled={!canSave} loading={rename.isPending}
                                onClick={save}>
                        <FiCheck/>
                    </IconButton>
                    <IconButton aria-label="Cancel" size="xs" variant="ghost" onClick={() => setEditing(false)}>
                        <FiX/>
                    </IconButton>
                </HStack>
            ) : (
                <HStack gap="2">
                    <Text>{collection.name}</Text>
                    <Badge size="sm" variant="subtle">{collection.item_count}</Badge>
                </HStack>
            )}
            {!editing && (
                <HStack gap="0">
                    <IconButton asChild aria-label="Show items" size="xs" variant="ghost">
                        <RouterLink to={`/library?q=${encodeURIComponent(`${searchTag}:${quoteSearchValue(collection.name)}`)}`}>
                            <FiSearch/>
                        </RouterLink>
                    </IconButton>
                    {canWrite && (
                        <>
                            <IconButton aria-label="Rename" size="xs" variant="ghost" onClick={startEditing}>
                                <FiEdit2/>
                            </IconButton>
                            <IconButton aria-label="Delete" size="xs" variant="ghost" colorPalette="red"
                                        onClick={() => setConfirm(true)}>
                                <FiTrash2/>
                            </IconButton>
                        </>
                    )}
                </HStack>
            )}
            <ConfirmDialog open={confirm} onOpenChange={setConfirm} destructive title={`Delete "${collection.name}"?`}
                           confirmLabel="Delete" loading={remove.isPending} onConfirm={() => remove.mutate(undefined, {onSuccess: () => setConfirm(false)})}>
                Items stay in your library; only this {kind === "tags" ? "tag" : "group"} is removed from them.
            </ConfirmDialog>
        </HStack>
    );
};

const CollectionList = ({kind, game}: { kind: CollectionKind; game: Game }) => {
    const [newName, setNewName] = useState("");
    const canWrite = usePermissions()("ITEMS_WRITE");
    const {data} = useCollections(kind, game.id);
    const create = useCreateCollection(kind);
    const submit = () => create.mutate({name: newName.trim(), gameId: game.id}, {onSuccess: () => setNewName("")});
    return (
        <Stack gap="1">
            <Heading size="sm">{kind === "tags" ? "Tags" : "Groups"}</Heading>
            {data?.length === 0 && <Text textStyle="hint">None yet.</Text>}
            {data?.map(collection => <CollectionRow key={collection.id} kind={kind} collection={collection}/>)}
            {canWrite && <HStack mt="2">
                <Input size="sm" placeholder={kind === "tags" ? "New tag" : "New group"} value={newName}
                       onChange={event => setNewName(event.target.value)}
                       onKeyDown={event => {
                           if (event.key === "Enter" && newName.trim()) submit();
                       }}/>
                <Button size="sm" disabled={!newName.trim()} loading={create.isPending}
                        onClick={submit}>Add</Button>
            </HStack>}
        </Stack>
    );
};

export const OrganizePage = () => {
    const {data: games} = useGames();
    const relevant = (games ?? []).filter(game => game.item_count > 0 && game.id !== UNKNOWN_GAME_ID);
    return (
        <Container maxW="7xl" py="6">
            <Stack gap="6">
                <Box>
                    <Heading size="xl">Organize</Heading>
                    <Text color="fg.muted">Tags and groups are personal and belong to one game.</Text>
                </Box>
                {relevant.length === 0 && (
                    <EmptyState.Root>
                        <EmptyState.Content>
                            <EmptyState.Indicator><FiFolder/></EmptyState.Indicator>
                            <VStack textAlign="center">
                                <EmptyState.Title>No games yet</EmptyState.Title>
                                <EmptyState.Description>Add items to your library first.</EmptyState.Description>
                            </VStack>
                        </EmptyState.Content>
                    </EmptyState.Root>
                )}
                <Grid templateColumns={{base: "1fr", lg: "repeat(2, 1fr)"}} gap="4">
                    {relevant.map(game => (
                        <Card.Root key={game.id} overflow="hidden">
                            {game.cover_url && <Image src={game.cover_url} alt="" h="100px" objectFit="cover"/>}
                            <Card.Body gap="4">
                                <HStack justify="space-between">
                                    <HStack>
                                        <Heading size="md">{game.name}</Heading>
                                        {game.name_pending && <Badge variant="subtle">fetching name…</Badge>}
                                    </HStack>
                                    <Button asChild size="xs" variant="outline">
                                        <RouterLink to={`/library?game=${game.id}`}>{game.item_count} item(s)</RouterLink>
                                    </Button>
                                </HStack>
                                <Grid templateColumns={{base: "1fr", sm: "1fr 1fr"}} gap="6">
                                    <CollectionList kind="tags" game={game}/>
                                    <CollectionList kind="groups" game={game}/>
                                </Grid>
                            </Card.Body>
                        </Card.Root>
                    ))}
                </Grid>
            </Stack>
        </Container>
    );
};

export default OrganizePage;
