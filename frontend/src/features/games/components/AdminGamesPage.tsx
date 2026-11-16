import {Badge, Button, Heading, HStack, IconButton, Input, NativeSelect, Stack, Table, Text} from "@chakra-ui/react";
import {useState} from "react";
import {FiCheck, FiEdit2, FiX} from "react-icons/fi";
import {UNKNOWN_GAME_ID, useGames} from "@/features/games/queries.ts";
import {useAddGameCompatibility, useRemoveGameCompatibility, useRenameGame} from "@/features/games/mutations.ts";
import type {Game} from "@/features/games/types.ts";

const GameRow = ({game, games}: { game: Game; games: Game[] }) => {
    const [editing, setEditing] = useState(false);
    const [name, setName] = useState(game.name);
    const [compatTarget, setCompatTarget] = useState("");
    const rename = useRenameGame(game.id);
    const addCompat = useAddGameCompatibility(game.id);
    const removeCompat = useRemoveGameCompatibility(game.id);
    const nameOf = (id: number) => games.find(g => g.id === id)?.name ?? `#${id}`;
    const canSave = name.trim() !== "" && name.trim() !== game.name;
    const startEditing = () => {
        setName(game.name);
        setEditing(true);
    };
    const save = () => rename.mutate(name.trim(), {onSuccess: () => setEditing(false)});
    return (
        <Table.Row>
            <Table.Cell>
                {editing ? (
                    <HStack>
                        <Input size="sm" value={name} autoFocus onChange={event => setName(event.target.value)}
                               onKeyDown={event => {
                                   if (event.key === "Enter" && canSave) save();
                                   if (event.key === "Escape") setEditing(false);
                               }}/>
                        <IconButton aria-label="Save" size="xs" disabled={!canSave} loading={rename.isPending} onClick={save}><FiCheck/></IconButton>
                        <IconButton aria-label="Cancel" size="xs" variant="ghost" onClick={() => setEditing(false)}><FiX/></IconButton>
                    </HStack>
                ) : (
                    <HStack>
                        <Text>{game.name}</Text>
                        {game.name_pending && <Badge variant="subtle">fetching name…</Badge>}
                        <IconButton aria-label="Rename" size="2xs" variant="ghost" onClick={startEditing}><FiEdit2/></IconButton>
                    </HStack>
                )}
            </Table.Cell>
            <Table.Cell>
                <HStack gap="1" wrap="wrap">
                    {game.modspace_ids.map(m => <Badge key={`${m.modspace}-${m.external_id}`} variant="surface">{m.modspace}: {m.external_id}</Badge>)}
                </HStack>
            </Table.Cell>
            <Table.Cell>
                <Stack gap="1">
                    <HStack gap="1" wrap="wrap">
                        {game.compatible_game_ids.map(id => (
                            <Badge key={id} colorPalette="cyan">
                                {nameOf(id)}
                                <IconButton aria-label={`Remove compatibility with ${nameOf(id)}`} size="2xs" variant="plain"
                                            onClick={() => removeCompat.mutate(id)}><FiX/></IconButton>
                            </Badge>
                        ))}
                    </HStack>
                    <HStack>
                        <NativeSelect.Root size="xs" w="40">
                            <NativeSelect.Field value={compatTarget} onChange={event => setCompatTarget(event.target.value)}>
                                <option value="">Compatible with…</option>
                                {games.filter(g => g.id !== game.id && !game.compatible_game_ids.includes(g.id)).map(g => (
                                    <option key={g.id} value={g.id}>{g.name}</option>
                                ))}
                            </NativeSelect.Field>
                            <NativeSelect.Indicator/>
                        </NativeSelect.Root>
                        <Button size="xs" variant="outline" disabled={!compatTarget} loading={addCompat.isPending}
                                onClick={() => addCompat.mutate(Number(compatTarget), {onSuccess: () => setCompatTarget("")})}>Add</Button>
                    </HStack>
                </Stack>
            </Table.Cell>
        </Table.Row>
    );
};

export const AdminGamesPage = () => {
    const games = useGames().data?.filter(game => game.id !== UNKNOWN_GAME_ID);
    return (
        <Stack gap="4">
            <Heading size="lg">Games</Heading>
            <Text textStyle="hint">
                Games are created automatically from modspace ids (e.g. Steam app ids) when items are registered.
                A compatibility link is one-way and is listed on the game it was added to; add it on both games
                for a two-way link.
            </Text>
            <Table.ScrollArea maxW="full">
                <Table.Root>
                    <Table.Header>
                        <Table.Row>
                            <Table.ColumnHeader>Name</Table.ColumnHeader>
                            <Table.ColumnHeader>Modspace ids</Table.ColumnHeader>
                            <Table.ColumnHeader>Compatibility</Table.ColumnHeader>
                        </Table.Row>
                    </Table.Header>
                    <Table.Body>
                        {games?.map(game => <GameRow key={game.id} game={game} games={games}/>)}
                    </Table.Body>
                </Table.Root>
            </Table.ScrollArea>
        </Stack>
    );
};

export default AdminGamesPage;
