import {Box, Button, Flex, HStack, Menu, Portal, Text} from "@chakra-ui/react";
import {useState} from "react";
import {FiChevronDown, FiFolderPlus, FiHardDrive, FiRefreshCw, FiTag, FiTrash2, FiX} from "react-icons/fi";
import {useBulkAutoBackup, useBulkBackup, useBulkRefresh, useBulkUnregister} from "@/features/items/mutations.ts";
import {ConfirmDialog} from "@/features/shared/ConfirmDialog.tsx";
import {CollectionPickerDialog} from "@/features/collections/components/CollectionPickerDialog.tsx";
import {notifySuccess} from "@/lib/notify.ts";
import type {CollectionKind} from "@/features/collections/types.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import type {ItemSummary} from "@/features/items/types.ts";

interface BulkActionsBarProps {
    selectedItems: ItemSummary[];
    onClear: () => void;
    onSelectAll: () => void;
}

export const BulkActionsBar = ({selectedItems, onClear, onSelectAll}: BulkActionsBarProps) => {
    const ids = selectedItems.map(item => item.id);
    const [confirmUnregister, setConfirmUnregister] = useState(false);
    const can = usePermissions();
    const canWrite = can("ITEMS_WRITE");
    const canImport = can("CONTENT_IMPORT");
    const [picker, setPicker] = useState<{ kind: CollectionKind; mode: "add" | "remove" } | null>(null);

    const gameIds = new Set(selectedItems.map(item => item.game.id));
    const singleGame = gameIds.size === 1 ? selectedItems[0].game.id : null;

    const unregister = useBulkUnregister();
    const autoBackup = useBulkAutoBackup();
    const refresh = useBulkRefresh();
    const backup = useBulkBackup();
    const changeAutoBackup = (enabled: boolean) => autoBackup.mutate({ids, enabled}, {
        onSuccess: result => notifySuccess(`Automatic backups changed for ${result.affected} item(s)`),
    });

    return (
        <Box position="sticky" bottom="4" zIndex="sticky" px="4">
            <Flex bg="bg.panel" borderWidth="1px" shadow="lg" borderRadius="xl" p="3" align="center" gap="3" wrap="wrap"
                  maxW="5xl" mx="auto">
                <Text fontWeight="semibold">{ids.length} selected</Text>
                <Button size="xs" variant="ghost" onClick={onSelectAll}>Select page</Button>
                <Button size="xs" variant="ghost" onClick={onClear}><FiX/> Clear</Button>
                <HStack gap="2" ms="auto" wrap="wrap">
                    {canWrite && <Menu.Root>
                        <Menu.Trigger asChild>
                            <Button size="sm" variant="outline" disabled={ids.length === 0}>
                                <FiTag/> Organize <FiChevronDown/>
                            </Button>
                        </Menu.Trigger>
                        <Portal>
                            <Menu.Positioner>
                                <Menu.Content>
                                    <Menu.Item value="tag-add" onClick={() => setPicker({kind: "tags", mode: "add"})}>
                                        <FiTag/> Add tag
                                    </Menu.Item>
                                    <Menu.Item value="tag-remove"
                                               onClick={() => setPicker({kind: "tags", mode: "remove"})}>
                                        <FiTag/> Remove tag
                                    </Menu.Item>
                                    <Menu.Item value="group-add"
                                               onClick={() => setPicker({kind: "groups", mode: "add"})}>
                                        <FiFolderPlus/> Add to group
                                    </Menu.Item>
                                    <Menu.Item value="group-remove"
                                               onClick={() => setPicker({kind: "groups", mode: "remove"})}>
                                        <FiFolderPlus/> Remove from group
                                    </Menu.Item>
                                </Menu.Content>
                            </Menu.Positioner>
                        </Portal>
                    </Menu.Root>}
                    {canWrite && <Menu.Root>
                        <Menu.Trigger asChild>
                            <Button size="sm" variant="outline" disabled={ids.length === 0}
                                    loading={autoBackup.isPending}>
                                <FiHardDrive/> Automatic backups <FiChevronDown/>
                            </Button>
                        </Menu.Trigger>
                        <Portal>
                            <Menu.Positioner>
                                <Menu.Content>
                                    {canImport && (
                                        <Menu.Item value="on" onClick={() => changeAutoBackup(true)}>
                                            Turn on (local items are skipped)
                                        </Menu.Item>
                                    )}
                                    <Menu.Item value="off" onClick={() => changeAutoBackup(false)}>
                                        Turn off
                                    </Menu.Item>
                                </Menu.Content>
                            </Menu.Positioner>
                        </Portal>
                    </Menu.Root>}
                    {canImport && (
                        <Button size="sm" variant="outline" disabled={ids.length === 0} loading={backup.isPending}
                                onClick={() => backup.mutate(ids, {
                                    onSuccess: result => notifySuccess(`Backup queued for ${result.affected} item(s)`),
                                })}>
                            <FiHardDrive/> Backup now
                        </Button>
                    )}
                    {can("ITEMS_REGISTER") && (
                        <Button size="sm" variant="outline" disabled={ids.length === 0} loading={refresh.isPending}
                                onClick={() => refresh.mutate(ids, {
                                    onSuccess: result => notifySuccess(`${result.affected} item(s) refreshed from their modspace`),
                                })}>
                            <FiRefreshCw/> Refresh
                        </Button>
                    )}
                    {canWrite && (
                        <Button size="sm" colorPalette="red" variant="subtle" disabled={ids.length === 0}
                                onClick={() => setConfirmUnregister(true)}>
                            <FiTrash2/> Remove
                        </Button>
                    )}
                </HStack>
            </Flex>
            <ConfirmDialog open={confirmUnregister} onOpenChange={setConfirmUnregister} destructive
                           title="Remove from library?" confirmLabel="Remove" loading={unregister.isPending}
                           onConfirm={() => unregister.mutate(ids, {
                               onSuccess: result => {
                                   notifySuccess(`${result.affected} item(s) removed from your library`);
                                   setConfirmUnregister(false);
                                   onClear();
                               },
                           })}>
                {`${ids.length} item(s) will be removed from your library together with your notes, tags, groups and ratings of them. Shared backups are kept for other users.`}
            </ConfirmDialog>
            {picker && (
                <CollectionPickerDialog open onOpenChange={open => !open && setPicker(null)} kind={picker.kind}
                                        mode={picker.mode} gameId={singleGame} itemIds={ids}/>
            )}
        </Box>
    );
};

export default BulkActionsBar;
