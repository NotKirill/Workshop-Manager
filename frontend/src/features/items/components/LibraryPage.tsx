import {
    Box,
    Button,
    Center,
    Container,
    Flex,
    Grid,
    HStack,
    IconButton,
    Input,
    InputGroup,
    Spinner,
    Stack,
    Text,
} from "@chakra-ui/react";
import {useMemo, useState} from "react";
import {useSearchParams} from "react-router";
import {FiCheckSquare, FiFilter, FiPlus, FiSearch, FiX} from "react-icons/fi";
import {useItemSearch} from "@/features/items/queries.ts";
import {ItemCard} from "@/features/items/components/ItemCard.tsx";
import {AddItemsDialog} from "@/features/items/components/AddItemsDialog.tsx";
import {BulkActionsBar} from "@/features/items/components/BulkActionsBar.tsx";
import {SearchHelpPopover} from "@/features/items/components/SearchHelpPopover.tsx";
import {UNKNOWN_GAME_ID, useGames} from "@/features/games/queries.ts";
import {getErrorMessage} from "@/lib/errors.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import type {ItemSummary} from "@/features/items/types.ts";
import {ErrorAlert} from "@/features/shared/ErrorAlert.tsx";
import {buildQuery, emptyFilters, type Filters} from "@/features/items/components/library/libraryQuery.ts";
import {LibraryFilters} from "@/features/items/components/library/LibraryFilters.tsx";
import {UnknownGameNotice} from "@/features/items/components/library/UnknownGameNotice.tsx";
import {LibraryEmptyState} from "@/features/items/components/library/LibraryEmptyState.tsx";

export const LibraryPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const [text, setText] = useState(searchParams.get("q") ?? "");
    const [submittedText, setSubmittedText] = useState(searchParams.get("q") ?? "");
    const [filters, setFilters] = useState<Filters>(() => ({...emptyFilters, game: searchParams.get("game") ?? ""}));
    const [showFilters, setShowFilters] = useState(true);
    const [addOpen, setAddOpen] = useState(false);
    const can = usePermissions();
    const canAdd = can("ITEMS_REGISTER") || can("ITEMS_WRITE");
    const [selecting, setSelecting] = useState(false);
    const [selected, setSelected] = useState<Map<number, ItemSummary>>(new Map());

    const {data: games} = useGames();
    const gameId = filters.game ? Number(filters.game) : undefined;

    const query = useMemo(() => buildQuery(submittedText, filters), [submittedText, filters]);
    const items = useItemSearch(query);

    const allItems = items.data?.pages.flatMap(page => page.items) ?? [];
    const total = items.data?.pages[0]?.total_elements ?? 0;

    const updateFilter = (key: keyof Filters, value: string) => {
        setFilters(previous => {
            const next = {...previous, [key]: value};
            if (key === "game") {
                next.tag = "";
                next.group = "";
            }
            return next;
        });
    };

    const submit = (value = text) => {
        setSubmittedText(value);
        setSearchParams(value ? {q: value} : {}, {replace: true});
    };

    const toggle = (id: number) => {
        setSelected(previous => {
            const next = new Map(previous);
            if (next.has(id)) {
                next.delete(id);
            } else {
                const item = allItems.find(i => i.id === id);
                if (item) next.set(id, item);
            }
            return next;
        });
    };

    const unknownGameItems = games?.find(game => game.id === UNKNOWN_GAME_ID)?.item_count ?? 0;
    const showingUnknownGame = gameId === UNKNOWN_GAME_ID;

    return (
        <Container maxW="8xl" py="6">
            <Flex gap="6" align="start">
                {showFilters && (
                    <Box w="250px" flexShrink={0} hideBelow="md" position="sticky" top="calc({sizes.header} + {spacing.6})">
                        <LibraryFilters filters={filters} onChange={updateFilter}
                                        onReset={() => setFilters(emptyFilters)}/>
                    </Box>
                )}

                <Box flex="1" minW="0">
                    <Stack gap="4">
                        <HStack gap="2">
                            <IconButton aria-label="Toggle filters" variant="outline" hideBelow="md"
                                        onClick={() => setShowFilters(value => !value)}>
                                <FiFilter/>
                            </IconButton>
                            <InputGroup flex="1" startElement={<FiSearch/>}
                                        endElement={text ? (
                                            <IconButton aria-label="Clear" size="xs" variant="ghost"
                                                        onClick={() => {
                                                            setText("");
                                                            submit("");
                                                        }}>
                                                <FiX/>
                                            </IconButton>
                                        ) : undefined}>
                                <Input placeholder='Search… e.g. map tag:favorite rating:>=8'
                                       value={text}
                                       onChange={event => setText(event.target.value)}
                                       onKeyDown={event => {
                                           if (event.key === "Enter") submit();
                                       }}/>
                            </InputGroup>
                            <SearchHelpPopover onExample={example => {
                                setText(example);
                                submit(example);
                            }}/>
                            <Button variant={selecting ? "solid" : "outline"} colorPalette={selecting ? "brand" : "gray"}
                                    aria-label="Select items" onClick={() => {
                                        setSelecting(value => !value);
                                        setSelected(new Map());
                                    }}>
                                <FiCheckSquare/> <Text hideBelow="sm">Select</Text>
                            </Button>
                            {canAdd && (
                                <Button colorPalette="brand" aria-label="Add items" onClick={() => setAddOpen(true)}>
                                    <FiPlus/> <Text hideBelow="sm">Add items</Text>
                                </Button>
                            )}
                        </HStack>

                        {unknownGameItems > 0 && (
                            <UnknownGameNotice count={unknownGameItems} showing={showingUnknownGame}
                                               onToggle={() => updateFilter("game", showingUnknownGame ? "" : String(UNKNOWN_GAME_ID))}/>
                        )}

                        <HStack justify="space-between" color="fg.muted" fontSize="sm">
                            <Text>{items.isSuccess ? `${total} item(s)` : ""}</Text>
                            {query && <Text truncate maxW="60%" title={query}>Query: {query}</Text>}
                        </HStack>

                        {items.isError && (
                            <ErrorAlert title="Search failed" description={getErrorMessage(items.error)}/>
                        )}

                        {items.isLoading && <Center py="20"><Spinner size="lg"/></Center>}

                        {items.isSuccess && allItems.length === 0 && (
                            <LibraryEmptyState hasQuery={!!query} canAdd={canAdd} onAdd={() => setAddOpen(true)}/>
                        )}

                        <Grid templateColumns="repeat(auto-fill, minmax(230px, 1fr))" gap="4">
                            {allItems.map(item => (
                                <ItemCard key={item.id} item={item} selectable={selecting}
                                          selected={selected.has(item.id)} onToggle={toggle}/>
                            ))}
                        </Grid>

                        {items.hasNextPage && (
                            <Center>
                                <Button variant="outline" loading={items.isFetchingNextPage}
                                        onClick={() => void items.fetchNextPage()}>
                                    Load more
                                </Button>
                            </Center>
                        )}
                    </Stack>
                </Box>
            </Flex>

            {selecting && selected.size > 0 && (
                <BulkActionsBar selectedItems={[...selected.values()]}
                                onClear={() => setSelected(new Map())}
                                onSelectAll={() => setSelected(new Map(allItems.map(item => [item.id, item])))}/>
            )}

            <AddItemsDialog open={addOpen} onOpenChange={setAddOpen}/>
        </Container>
    );
};

export default LibraryPage;
