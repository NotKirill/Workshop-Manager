import {
    Alert,
    Badge,
    Box,
    Button,
    HStack,
    IconButton,
    Input,
    NumberInput,
    Progress,
    Stack,
    Table,
    Text,
} from "@chakra-ui/react";
import {useRef, useState} from "react";
import {FiDownload, FiHardDrive, FiTrash2, FiUpload} from "react-icons/fi";
import {
    useDeleteAllContent,
    useDeleteRevision,
    useDownloadRevision,
    useRequestBackup,
    useSetMaxRevisions,
    useUploadContent
} from "@/features/items/mutations.ts";
import {ConfirmDialog} from "@/features/shared/ConfirmDialog.tsx";
import {formatBytes, formatDate} from "@/lib/format.ts";
import {notifyInfo, notifySuccess} from "@/lib/notify.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import type {ContentJobError, ItemDetail} from "@/features/items/types.ts";

const jobErrorMessages: Record<ContentJobError, string> = {
    DOWNLOAD_UNAVAILABLE: "downloading from the source is not set up on this server",
    NOT_DOWNLOADABLE: "the source does not allow downloading this item (e.g. the game must be owned)",
    DOWNLOAD_TIMEOUT: "the download took too long",
    DOWNLOAD_FAILED: "the download failed",
    INTERNAL_ERROR: "an internal error happened, an administrator can see the details",
};

export const ContentPanel = ({item}: { item: ItemDetail }) => {
    const fileInput = useRef<HTMLInputElement>(null);
    const [progress, setProgress] = useState<number | null>(null);
    const [confirmDeleteAll, setConfirmDeleteAll] = useState(false);
    const {summary, can_backup: canBackup, can_upload: canUpload, can_delete: canDelete} = item.content;
    const can = usePermissions();
    const mayImport = can("CONTENT_IMPORT");
    const mayWrite = can("ITEMS_WRITE");
    const mayDelete = canDelete && mayWrite;
    const [maxRevisions, setMaxRevisions] = useState(String(summary.max_revisions));

    const backup = useRequestBackup(item.id);
    const upload = useUploadContent(item.id);
    const download = useDownloadRevision(item.id);
    const deleteRevision = useDeleteRevision(item.id);
    const deleteAll = useDeleteAllContent(item.id);
    const saveMax = useSetMaxRevisions(item.id);

    return (
        <Stack gap="4">
            <HStack justify="space-between" wrap="wrap" gap="2">
                <Text textStyle="hint">
                    Automatic backups: <b>{item.personal.auto_backup ? "on" : "off"}</b>. Backups downloaded from the
                    source are shared with everyone who has this item; your uploads are visible only to you.
                </Text>
                <HStack>
                    {canBackup && mayImport && (
                        <Button size="sm" colorPalette="brand" loading={backup.isPending}
                                disabled={!!summary.active_job} onClick={() => backup.mutate(undefined, {
                                    onSuccess: () => notifyInfo("Backup queued",
                                        "The download runs in the background; refresh in a moment."),
                                })}>
                            <FiHardDrive/> Backup now
                        </Button>
                    )}
                    {canUpload && mayImport && (
                        <>
                            <Input ref={fileInput} type="file" display="none" onChange={event => {
                                const file = event.target.files?.[0];
                                if (file) upload.mutate({file, onProgress: setProgress}, {
                                    onSuccess: revision => notifySuccess(`Revision ${revision.revision} stored`),
                                    onSettled: () => setProgress(null),
                                });
                                event.target.value = "";
                            }}/>
                            <Button size="sm" variant="outline" loading={upload.isPending}
                                    onClick={() => fileInput.current?.click()}>
                                <FiUpload/> Upload file
                            </Button>
                        </>
                    )}
                </HStack>
            </HStack>

            {!canBackup && item.modspace !== "LOCAL" && (
                <Alert.Root status="info" size="sm">
                    <Alert.Indicator/>
                    <Alert.Description>
                        Automatic downloads need SteamCMD on the server.{mayImport && " You can still upload files yourself."}
                    </Alert.Description>
                </Alert.Root>
            )}

            {progress !== null && (
                <Progress.Root value={progress} colorPalette="brand">
                    <Progress.Track><Progress.Range/></Progress.Track>
                </Progress.Root>
            )}

            {summary.active_job && (
                <Alert.Root status="info">
                    <Alert.Indicator/>
                    <Alert.Description>
                        {summary.active_job.type === "BACKUP" ? "Backup" : "Deletion"} {summary.active_job.status.toLowerCase()}
                        {summary.active_job.attempts > 1 && ` (attempt ${summary.active_job.attempts})`}
                        {summary.active_job.error_code
                            && ` — last attempt failed: ${jobErrorMessages[summary.active_job.error_code]}`}
                    </Alert.Description>
                </Alert.Root>
            )}
            {summary.last_failed_job && !summary.active_job && (
                <Alert.Root status="warning">
                    <Alert.Indicator/>
                    <Alert.Description>
                        Last {summary.last_failed_job.type.toLowerCase().replace("_", " ")} failed
                        after {summary.last_failed_job.attempts} attempt(s)
                        {summary.last_failed_job.error_code && `: ${jobErrorMessages[summary.last_failed_job.error_code]}`}
                    </Alert.Description>
                </Alert.Root>
            )}

            {summary.revisions.length === 0 ? (
                <Box borderWidth="1px" borderStyle="dashed" borderRadius="md" p="6" textAlign="center" color="fg.muted">
                    No backups stored yet.
                </Box>
            ) : (
                <Table.ScrollArea maxW="full">
                    <Table.Root>
                        <Table.Header>
                            <Table.Row>
                                <Table.ColumnHeader>Rev.</Table.ColumnHeader>
                                <Table.ColumnHeader>File</Table.ColumnHeader>
                                <Table.ColumnHeader>Size</Table.ColumnHeader>
                                <Table.ColumnHeader>Stored</Table.ColumnHeader>
                                <Table.ColumnHeader textAlign="end">Actions</Table.ColumnHeader>
                            </Table.Row>
                        </Table.Header>
                        <Table.Body>
                            {summary.revisions.map((revision, index) => (
                                <Table.Row key={revision.revision}>
                                    <Table.Cell>
                                        {revision.revision} {index === 0 && <Badge colorPalette="green" size="sm">latest</Badge>}
                                        {revision.uploaded && <Badge colorPalette="purple" size="sm" ms="1">your upload</Badge>}
                                    </Table.Cell>
                                    <Table.Cell maxW={{base: "40", md: "xs"}} truncate
                                                title={revision.sha256
                                                    ? `${revision.file_name}\nSHA-256 ${revision.sha256}`
                                                    : revision.file_name}>
                                        {revision.file_name}
                                    </Table.Cell>
                                    <Table.Cell>{formatBytes(revision.file_size)}</Table.Cell>
                                    <Table.Cell>{formatDate(revision.time_created)}</Table.Cell>
                                    <Table.Cell textAlign="end">
                                        <HStack justify="flex-end" gap="1">
                                            <IconButton aria-label="Download" size="xs" variant="ghost"
                                                        onClick={() => download.mutate(revision.revision)}>
                                                <FiDownload/>
                                            </IconButton>
                                            {(mayDelete || (mayWrite && revision.uploaded)) && (
                                                <IconButton aria-label="Delete revision" size="xs" variant="ghost"
                                                            colorPalette="red"
                                                            onClick={() => deleteRevision.mutate(revision.revision,
                                                                {onSuccess: () => notifyInfo("Deletion queued")})}>
                                                    <FiTrash2/>
                                                </IconButton>
                                            )}
                                        </HStack>
                                    </Table.Cell>
                                </Table.Row>
                            ))}
                        </Table.Body>
                    </Table.Root>
                </Table.ScrollArea>
            )}

            {mayDelete && (
                <HStack justify="space-between" wrap="wrap" gap="3">
                    <HStack>
                        <Text fontSize="sm">Keep last</Text>
                        <NumberInput.Root size="xs" w="20" min={1} max={50} value={maxRevisions}
                                          onValueChange={event => setMaxRevisions(event.value)}>
                            <NumberInput.Control/>
                            <NumberInput.Input/>
                        </NumberInput.Root>
                        <Text fontSize="sm" title="Shared backups and each user's uploads are counted separately">
                            revisions
                        </Text>
                        <Button size="xs" variant="outline" loading={saveMax.isPending}
                                disabled={Number(maxRevisions) === summary.max_revisions}
                                onClick={() => saveMax.mutate(Number(maxRevisions),
                                    {onSuccess: () => notifySuccess("Retention updated")})}>Save</Button>
                    </HStack>
                    {summary.revisions.length > 0 && (
                        <Button size="xs" colorPalette="red" variant="subtle" onClick={() => setConfirmDeleteAll(true)}>
                            <FiTrash2/> Delete all backups
                        </Button>
                    )}
                </HStack>
            )}

            <ConfirmDialog open={confirmDeleteAll} onOpenChange={setConfirmDeleteAll} destructive
                           title="Delete all backups?" confirmLabel="Delete" loading={deleteAll.isPending}
                           onConfirm={() => deleteAll.mutate(undefined, {
                               onSuccess: () => {
                                   notifyInfo("All backups will be deleted");
                                   setConfirmDeleteAll(false);
                               },
                           })}>
                Every stored revision of this item, including other users' uploads, will be deleted from storage. This
                cannot be undone.
            </ConfirmDialog>
        </Stack>
    );
};

export default ContentPanel;
