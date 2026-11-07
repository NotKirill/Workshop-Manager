import {Button, Field, Heading, HStack, NativeSelect, Stack} from "@chakra-ui/react";
import type {ReactNode} from "react";
import {useCollections} from "@/features/collections/queries.ts";
import {UNKNOWN_GAME_ID, useGames} from "@/features/games/queries.ts";
import type {Filters} from "@/features/items/components/library/libraryQuery.ts";

const FilterSelect = ({label, value, onChange, children}: {
    label: string;
    value: string;
    onChange: (value: string) => void;
    children: ReactNode;
}) => (
    <Field.Root>
        <Field.Label fontSize="sm">{label}</Field.Label>
        <NativeSelect.Root size="sm">
            <NativeSelect.Field value={value} onChange={event => onChange(event.target.value)}>
                {children}
            </NativeSelect.Field>
            <NativeSelect.Indicator/>
        </NativeSelect.Root>
    </Field.Root>
);

interface LibraryFiltersProps {
    filters: Filters;
    onChange: (key: keyof Filters, value: string) => void;
    onReset: () => void;
}

export const LibraryFilters = ({filters, onChange, onReset}: LibraryFiltersProps) => {
    const {data: games} = useGames();
    const gameId = filters.game ? Number(filters.game) : undefined;
    // The unknown game has no tags or groups
    const organizable = !!gameId && gameId !== UNKNOWN_GAME_ID;
    const {data: tags} = useCollections("tags", gameId, organizable);
    const {data: groups} = useCollections("groups", gameId, organizable);

    const activeFilterCount = Object.values(filters).filter(Boolean).length;
    const gamesWithItems = games?.filter(game => game.item_count > 0 && game.id !== UNKNOWN_GAME_ID) ?? [];
    const showingUnknownGame = gameId === UNKNOWN_GAME_ID;

    return (
        <Stack gap="3" bg="bg.panel" borderWidth="1px" borderRadius="lg" p="4">
            <HStack justify="space-between">
                <Heading size="sm">Filters</Heading>
                {activeFilterCount > 0 && (
                    <Button size="xs" variant="ghost" onClick={onReset}>
                        Reset
                    </Button>
                )}
            </HStack>
            <FilterSelect label="Game" value={filters.game} onChange={v => onChange("game", v)}>
                <option value="">All games</option>
                {gamesWithItems.map(game => (
                    <option key={game.id} value={game.id}>{game.name} ({game.item_count})</option>
                ))}
                {showingUnknownGame && <option value={UNKNOWN_GAME_ID}>Unknown game</option>}
            </FilterSelect>
            {organizable && (
                <>
                    <FilterSelect label="Tag" value={filters.tag} onChange={v => onChange("tag", v)}>
                        <option value="">Any tag</option>
                        {tags?.map(tag => <option key={tag.id} value={tag.name}>{tag.name}</option>)}
                    </FilterSelect>
                    <FilterSelect label="Group" value={filters.group} onChange={v => onChange("group", v)}>
                        <option value="">Any group</option>
                        {groups?.map(group => (
                            <option key={group.id} value={group.name}>{group.name}</option>
                        ))}
                    </FilterSelect>
                </>
            )}
            <FilterSelect label="Source" value={filters.modspace} onChange={v => onChange("modspace", v)}>
                <option value="">Any source</option>
                <option value="steam">Steam Workshop</option>
                <option value="local">Local</option>
            </FilterSelect>
            <FilterSelect label="Backups" value={filters.backup} onChange={v => onChange("backup", v)}>
                <option value="">Any</option>
                <option value="yes">Has backups</option>
                <option value="no">No backups</option>
            </FilterSelect>
            <FilterSelect label="Automatic backups" value={filters.autoBackup}
                          onChange={v => onChange("autoBackup", v)}>
                <option value="">Any</option>
                <option value="yes">On</option>
                <option value="no">Off</option>
            </FilterSelect>
            <FilterSelect label="Availability" value={filters.status} onChange={v => onChange("status", v)}>
                <option value="">Any</option>
                <option value="available">Available</option>
                <option value="unavailable">Removed from source</option>
            </FilterSelect>
            <FilterSelect label="Sort by" value={filters.sort} onChange={v => onChange("sort", v)}>
                <option value="">Recently added</option>
                <option value="registered_asc">Oldest added</option>
                <option value="updated">Recently updated</option>
                <option value="created">Newest originals</option>
                <option value="rating">Best rated</option>
            </FilterSelect>
        </Stack>
    );
};

export default LibraryFilters;
