import {
    Badge,
    Box,
    Button,
    Checkbox,
    HStack,
    Input,
    Link,
    NativeSelect,
    Stack,
    Tabs,
    Text,
    Textarea,
} from "@chakra-ui/react";
import {useState} from "react";
import {Link as RouterLink} from "react-router";
import {useRegisterExternalItems, useRegisterLocalItem} from "@/features/items/mutations.ts";
import {AutoBackupCheckbox} from "@/features/items/components/AutoBackupCheckbox.tsx";
import {useGames} from "@/features/games/queries.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {getCodeMessage} from "@/lib/errors.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import {externalModspaces, parseReferences} from "@/features/items/modspaceReferences.ts";
import type {ModspaceType} from "@/lib/apiTypes.ts";
import type {RegistrationResult, RegistrationStatus} from "@/features/items/types.ts";
import {AppDialog} from "@/features/shared/AppDialog.tsx";
import {Field} from "@/features/shared/Field.tsx";

const statusColors: Record<RegistrationStatus, string> = {
    REGISTERED: "green",
    ALREADY_REGISTERED: "blue",
    INVALID: "red",
    FAILED: "red",
};

const REASON_TEXTS: Record<string, string> = {
    INVALID_ID: "Not a valid id for this modspace",
    SOURCE_UNAVAILABLE: "No longer available in the modspace; stored metadata is used",
    SOURCE_UNKNOWN: "The modspace does not show this item (removed, private or a wrong id); it is kept under the unknown game until the modspace shows it",
    CONCURRENT_CHANGE: "Changed at the same time, please retry",
};

const registrationReasonText = (reason: string): string =>
    REASON_TEXTS[reason] ?? getCodeMessage(reason) ?? "Could not be registered";

const ResultsList = ({results}: { results: RegistrationResult[] }) => (
    <Stack gap="1" maxH="260px" overflowY="auto" borderWidth="1px" borderRadius="md" p="2">
        {results.map(result => (
            <HStack key={`${result.modspace}-${result.original_id}-${result.dependency_of ?? ""}`}
                    justify="space-between" gap="2" fontSize="sm">
                <Box minW="0" flex="1">
                    {result.personal_id ? (
                        <Link asChild color="brand.fg">
                            <RouterLink to={`/items/${result.personal_id}`}>
                                {result.title ?? result.original_id}
                            </RouterLink>
                        </Link>
                    ) : (
                        <Text truncate>{result.original_id}</Text>
                    )}
                    {result.dependency_of && <Text as="span" color="fg.muted"> (dependency)</Text>}
                    {result.reason && (
                        <Text color="fg.muted" fontSize="xs">{registrationReasonText(result.reason)}</Text>
                    )}
                </Box>
                <Badge colorPalette={statusColors[result.status]}>{result.status.replace("_", " ")}</Badge>
            </HStack>
        ))}
    </Stack>
);

const ExternalForm = ({onDone}: { onDone: () => void }) => {
    const [references, setReferences] = useState("");
    const [idsModspace, setIdsModspace] = useState<ModspaceType>(externalModspaces[0].type);
    const [autoBackup, setAutoBackup] = useState(false);
    const [includeDependencies, setIncludeDependencies] = useState(false);
    const [results, setResults] = useState<RegistrationResult[] | null>(null);

    const parsed = parseReferences(references, idsModspace);

    const mutation = useRegisterExternalItems();
    const register = () => mutation.mutate({items: parsed.items, autoBackup, includeDependencies}, {
        onSuccess: data => {
            setResults(data.results);
            const added = data.results.filter(r => r.status === "REGISTERED").length;
            notifySuccess(`${added} item(s) added`);
        },
    });

    const count = parsed.count;
    return (
            <Stack gap="4">
                <Field invalid={parsed.unrecognized.length > 0} label="Links or ids" helperText={<>One per line (or separated by spaces/commas). {count > 0 && `${count} item(s).`}</>} errorText={<>Not recognized, will be skipped: {parsed.unrecognized.join(", ")}</>}>
                    <Textarea rows={6} value={references} onChange={event => setReferences(event.target.value)}
                              placeholder={"https://steamcommunity.com/sharedfiles/filedetails/?id=105982362\n180925247"}/>
                </Field>
                <Field label="Plain ids are from" helperText="Links are recognized by their address.">
                    <NativeSelect.Root>
                        <NativeSelect.Field value={idsModspace}
                                            onChange={event => setIdsModspace(event.target.value as ModspaceType)}>
                            {externalModspaces.map(modspace => (
                                <option key={modspace.type} value={modspace.type}>{modspace.label}</option>
                            ))}
                        </NativeSelect.Field>
                        <NativeSelect.Indicator/>
                    </NativeSelect.Root>
                </Field>
                <AutoBackupCheckbox checked={autoBackup} onChange={setAutoBackup}/>
                <Checkbox.Root checked={includeDependencies}
                               onCheckedChange={event => setIncludeDependencies(event.checked === true)}>
                    <Checkbox.HiddenInput/>
                    <Checkbox.Control/>
                    <Checkbox.Label>Also add required items (dependencies)</Checkbox.Label>
                </Checkbox.Root>
                {results && <ResultsList results={results}/>}
                <HStack justify="flex-end">
                    {results && <Button variant="ghost" onClick={onDone}>Close</Button>}
                    <Button colorPalette="brand" loading={mutation.isPending} disabled={count === 0}
                            onClick={register}>
                        Add {count > 0 ? count : ""} item(s)
                    </Button>
                </HStack>
            </Stack>
    );
};

const LocalForm = ({onDone}: { onDone: () => void }) => {
    const {data: games} = useGames();
    const [gameId, setGameId] = useState<string>("");
    const [newGame, setNewGame] = useState("");
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [commentary, setCommentary] = useState("");

    const mutation = useRegisterLocalItem();
    const create = () => mutation.mutate({
        game_id: gameId && gameId !== "new" ? Number(gameId) : null,
        game_name: gameId === "new" ? newGame : null,
        title,
        description: description || null,
        user_commentary: commentary || null,
    }, {
        onSuccess: () => {
            notifySuccess("Local item created", "Open it to upload files.");
            onDone();
        },
    });

    const valid = title.trim().length > 0 && (gameId !== "" && (gameId !== "new" || newGame.trim().length > 0));
    return (
        <Stack gap="4">
            <Field required label="Game">
                <NativeSelect.Root>
                    <NativeSelect.Field value={gameId} onChange={event => setGameId(event.target.value)}>
                        <option value="">Choose a game…</option>
                        {games?.map(game => <option key={game.id} value={game.id}>{game.name}</option>)}
                        <option value="new">+ New game…</option>
                    </NativeSelect.Field>
                    <NativeSelect.Indicator/>
                </NativeSelect.Root>
            </Field>
            {gameId === "new" && (
                <Field required label="New game name">
                    <Input value={newGame} onChange={event => setNewGame(event.target.value)}/>
                </Field>
            )}
            <Field required label="Title">
                <Input value={title} onChange={event => setTitle(event.target.value)}/>
            </Field>
            <Field label="Description">
                <Textarea rows={3} value={description} onChange={event => setDescription(event.target.value)}/>
            </Field>
            <Field label="Your notes">
                <Textarea rows={2} value={commentary} onChange={event => setCommentary(event.target.value)}/>
            </Field>
            <HStack justify="flex-end">
                <Button colorPalette="brand" loading={mutation.isPending} disabled={!valid}
                        onClick={create}>
                    Create local item
                </Button>
            </HStack>
        </Stack>
    );
};

export const AddItemsDialog = ({open, onOpenChange}: { open: boolean; onOpenChange: (open: boolean) => void }) => {
    const can = usePermissions();
    const canRegister = can("ITEMS_REGISTER");
    const canWrite = can("ITEMS_WRITE");
    return (
    <AppDialog open={open} onOpenChange={onOpenChange} title="Add items" size="lg" lazy closeButton>
        <Tabs.Root defaultValue={canRegister ? "steam" : "local"} variant="enclosed">
            <Tabs.List mb="4">
                {canRegister && <Tabs.Trigger value="steam">From a modspace</Tabs.Trigger>}
                {canWrite && <Tabs.Trigger value="local">Local item</Tabs.Trigger>}
            </Tabs.List>
            <Tabs.Content value="steam">
                <ExternalForm onDone={() => onOpenChange(false)}/>
            </Tabs.Content>
            <Tabs.Content value="local">
                <LocalForm onDone={() => onOpenChange(false)}/>
            </Tabs.Content>
        </Tabs.Root>
    </AppDialog>
    );
};

export default AddItemsDialog;
