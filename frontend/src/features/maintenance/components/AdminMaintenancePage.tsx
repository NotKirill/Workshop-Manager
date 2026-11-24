import {Badge, Button, Heading, HStack, IconButton, Stack, Table, Text} from "@chakra-ui/react";
import {FiPlay, FiRefreshCw, FiRotateCcw} from "react-icons/fi";
import {
    useFailedCleanups,
    useGivenUpMediaSyncs,
    useMaintenanceContentJobs,
    useMaintenanceOverview
} from "@/features/maintenance/queries.ts";
import {
    useRetryCleanup,
    useRetryContentJob,
    useRetryMediaSync,
    useRunAuthorRefresh,
    useRunCleanup,
    useRunContentJobs,
    useRunGameLookup,
    useRunMediaSyncs,
    useRunSync
} from "@/features/maintenance/mutations.ts";
import {formatRelative} from "@/lib/format.ts";
import {notifySuccess} from "@/lib/notify.ts";
import type {ContentJobStatus} from "@/lib/apiTypes.ts";
import type {UseMutationResult} from "@tanstack/react-query";
import {type ReactNode, useState} from "react";
import {ConfirmDialog} from "@/features/shared/ConfirmDialog.tsx";

const statusColors: Record<ContentJobStatus, string> = {
    PENDING: "blue",
    RUNNING: "blue",
    SUCCEEDED: "green",
    FAILED: "red",
    CANCELLED: "gray",
};

const RunButton = <T, >({label, icon, mutation, report, confirm}: {
    label: string;
    icon: ReactNode;
    mutation: UseMutationResult<T, Error, void>;
    report: (result: T) => string;
    confirm?: string;
}) => {
    const [asking, setAsking] = useState(false);
    const run = () => mutation.mutate(undefined, {
        onSuccess: result => {
            setAsking(false);
            notifySuccess(report(result));
        },
    });
    return (
        <>
            <Button size="sm" variant="outline" loading={mutation.isPending}
                    onClick={confirm ? () => setAsking(true) : run}>
                {icon} {label}
            </Button>
            {confirm && (
                <ConfirmDialog open={asking} onOpenChange={setAsking} destructive title={`${label} now?`}
                               confirmLabel={label} loading={mutation.isPending} onConfirm={run}>
                    {confirm}
                </ConfirmDialog>
            )}
        </>
    );
};

const RetryButton = ({pending, onClick}: { pending: boolean; onClick: () => void }) => (
    <IconButton aria-label="Retry" size="xs" variant="ghost" loading={pending} onClick={onClick}>
        <FiRotateCcw/>
    </IconButton>
);

export const AdminMaintenancePage = () => {
    const overview = useMaintenanceOverview();
    const failedCleanups = useFailedCleanups();
    const mediaSyncs = useGivenUpMediaSyncs();
    const jobs = useMaintenanceContentJobs();

    const runCleanup = useRunCleanup();
    const runSync = useRunSync();
    const runAuthorRefresh = useRunAuthorRefresh();
    const runGameLookup = useRunGameLookup();
    const runJobs = useRunContentJobs();
    const runMediaSyncs = useRunMediaSyncs();
    const retryMediaSync = useRetryMediaSync();
    const retryCleanup = useRetryCleanup();
    const retryJob = useRetryContentJob();

    const data = overview.data;
    return (
        <Stack gap="6">
            <HStack justify="space-between" wrap="wrap" gap="2">
                <Heading size="lg">Maintenance</Heading>
                <HStack wrap="wrap">
                    <RunButton label="Sync stale items" icon={<FiRefreshCw/>} mutation={runSync}
                               report={r => `Sync: ${r.synced} synced, ${r.unavailable} unavailable, ${r.backupsQueued} backups queued`}/>
                    <RunButton label="Refresh authors" icon={<FiRefreshCw/>} mutation={runAuthorRefresh}
                               report={r => `Authors: ${r.refreshed} refreshed, ${r.failed} failed`}/>
                    <RunButton label="Look up games" icon={<FiRefreshCw/>} mutation={runGameLookup}
                               report={r => `Game lookup: ${r.named} named, ${r.unknown} unknown, ${r.failed} failed`}/>
                    <RunButton label="Run content jobs" icon={<FiPlay/>} mutation={runJobs}
                               report={r => `${r.processed} content job(s) processed`}/>
                    <RunButton label="Copy modspace media" icon={<FiPlay/>} mutation={runMediaSyncs}
                               report={r => `Media copied for ${r.processed} item(s)`}/>
                    <RunButton label="Run cleanup" icon={<FiPlay/>} mutation={runCleanup}
                               confirm="Items that are in nobody's library anymore are deleted once their grace period is over, together with their backups and the media copied from the modspace. The scheduled cleanup does the same on its own."
                               report={r => `Cleanup: ${r.marked} marked, ${r.deleted} deleted, ${r.failed} failed`}/>
                </HStack>
            </HStack>

            {data && (
                <HStack fontSize="sm" gap="4" wrap="wrap">
                    <HStack>Steam API key {data.steam_api_key_configured ? <Badge colorPalette="green">set</Badge> : <Badge colorPalette="orange">missing</Badge>}</HStack>
                    <HStack>SteamCMD {data.steamcmd_configured ? <Badge colorPalette="green">ready</Badge> : <Badge colorPalette="orange">not configured</Badge>}</HStack>
                    <Text color="fg.muted">Counts, queues, quotas and trends are in Grafana ("Workshop Manager: library and jobs").</Text>
                </HStack>
            )}

            <Stack gap="2">
                <Heading size="sm">Failed cleanups (dead letters)</Heading>
                {failedCleanups.data?.length === 0 && <Text textStyle="hint">None.</Text>}
                {(failedCleanups.data?.length ?? 0) > 0 && (
                    <Table.ScrollArea maxW="full">
                        <Table.Root>
                            <Table.Body>
                                {failedCleanups.data?.map(item => (
                                    <Table.Row key={item.internal_id}>
                                        <Table.Cell>#{item.internal_id} {item.modspace}:{item.original_id}</Table.Cell>
                                        <Table.Cell>{item.attempts} attempt(s)</Table.Cell>
                                        <Table.Cell maxW="md" truncate title={item.last_error ?? ""}>{item.last_error}</Table.Cell>
                                        <Table.Cell textAlign="end">
                                            <RetryButton
                                                pending={retryCleanup.isPending && retryCleanup.variables === item.internal_id}
                                                onClick={() => retryCleanup.mutate(item.internal_id, {
                                                    onSuccess: () => notifySuccess("Cleanup will run again"),
                                                })}/>
                                        </Table.Cell>
                                    </Table.Row>
                                ))}
                            </Table.Body>
                        </Table.Root>
                    </Table.ScrollArea>
                )}
            </Stack>

            <Stack gap="2">
                <Heading size="sm">Modspace media given up</Heading>
                {mediaSyncs.data?.length === 0 && <Text textStyle="hint">None.</Text>}
                {(mediaSyncs.data?.length ?? 0) > 0 && (
                    <Table.ScrollArea maxW="full">
                        <Table.Root>
                            <Table.Body>
                                {mediaSyncs.data?.map(sync => (
                                    <Table.Row key={`${sync.entity}-${sync.entity_id}`}>
                                        <Table.Cell>{sync.entity} #{sync.entity_id} {sync.title}</Table.Cell>
                                        <Table.Cell>{sync.failures} failure(s)</Table.Cell>
                                        <Table.Cell maxW="md" truncate title={sync.last_error ?? ""}>{sync.last_error}</Table.Cell>
                                        <Table.Cell textAlign="end">
                                            <RetryButton
                                                pending={retryMediaSync.isPending && retryMediaSync.variables?.entity === sync.entity
                                                    && retryMediaSync.variables.entity_id === sync.entity_id}
                                                onClick={() => retryMediaSync.mutate(sync, {
                                                    onSuccess: () => notifySuccess("Media copy will run again"),
                                                })}/>
                                        </Table.Cell>
                                    </Table.Row>
                                ))}
                            </Table.Body>
                        </Table.Root>
                    </Table.ScrollArea>
                )}
            </Stack>

            <Stack gap="2">
                <Heading size="sm">Recent content jobs</Heading>
                <Table.ScrollArea maxW="full">
                    <Table.Root>
                        <Table.Header>
                            <Table.Row>
                                <Table.ColumnHeader>Job</Table.ColumnHeader>
                                <Table.ColumnHeader>Item</Table.ColumnHeader>
                                <Table.ColumnHeader>Status</Table.ColumnHeader>
                                <Table.ColumnHeader>Info</Table.ColumnHeader>
                                <Table.ColumnHeader/>
                            </Table.Row>
                        </Table.Header>
                        <Table.Body>
                            {jobs.data?.map(job => (
                                <Table.Row key={job.id}>
                                    <Table.Cell>#{job.id} {job.type}{job.revision ? ` r${job.revision}` : ""}</Table.Cell>
                                    <Table.Cell>{job.title ?? `#${job.internal_id}`}</Table.Cell>
                                    <Table.Cell>
                                        <Badge colorPalette={statusColors[job.status]}>{job.status}</Badge>
                                    </Table.Cell>
                                    <Table.Cell maxW="md" truncate title={job.note ?? job.last_error ?? ""}>
                                        {job.note ?? job.last_error ?? `created ${formatRelative(job.time_created)}`}
                                    </Table.Cell>
                                    <Table.Cell textAlign="end">
                                        {(job.status === "FAILED" || job.status === "CANCELLED") && (
                                            <RetryButton pending={retryJob.isPending && retryJob.variables === job.id}
                                                         onClick={() => retryJob.mutate(job.id, {
                                                             onSuccess: () => notifySuccess("Job queued again"),
                                                         })}/>
                                        )}
                                    </Table.Cell>
                                </Table.Row>
                            ))}
                        </Table.Body>
                    </Table.Root>
                </Table.ScrollArea>
            </Stack>
        </Stack>
    );
};

export default AdminMaintenancePage;
