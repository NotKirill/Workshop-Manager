import {Avatar, Badge, Box, Button, Card, Center, Flex, Heading, HStack, Image, Text} from "@chakra-ui/react";
import {FiArchive, FiEdit2, FiExternalLink, FiRefreshCw, FiTrash2} from "react-icons/fi";
import {useRefreshItem} from "@/features/items/mutations.ts";
import {formatBytes, formatRelative, modspaceLabels} from "@/lib/format.ts";
import {itemUrl} from "@/features/items/modspaceReferences.ts";
import {MEDIA_PURPOSES} from "@/features/items/mediaPurposes.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import type {ItemDetail} from "@/features/items/types.ts";

interface ItemHeaderProps {
    item: ItemDetail;
    onEdit: () => void;
    onRemove: () => void;
}

export const ItemHeader = ({item, onEdit, onRemove}: ItemHeaderProps) => {
    const can = usePermissions();
    const refresh = useRefreshItem(item.id);
    const isLocal = item.modspace === "LOCAL";
    const sourceUrl = itemUrl(item.modspace, item.original_id);
    const backupCount = item.content.summary.revisions.length;
    const preview = item.media.find(media => media.purpose === MEDIA_PURPOSES.PREVIEW && !media.hidden
        && media.type === "FILE");

    return (
        <Card.Root overflow="hidden">
            <Flex direction={{base: "column", md: "row"}}>
                <Box w={{base: "full", md: "320px"}} flexShrink={0} bg="bg.muted" minH="200px">
                    {preview?.url ? (
                        <Image src={preview.url} alt="" w="full" h="full" minH="200px" objectFit="cover"/>
                    ) : (
                        <Center h="full" color="fg.subtle"><FiArchive size={64}/></Center>
                    )}
                </Box>
                <Card.Body gap="3">
                    <HStack gap="2" wrap="wrap">
                        <Badge variant="subtle">{item.game.name}</Badge>
                        <Badge variant="outline">{modspaceLabels[item.modspace]}</Badge>
                        {item.source_status === "UNAVAILABLE" && (
                            <Badge colorPalette="red">Removed from {modspaceLabels[item.modspace]}</Badge>
                        )}
                        {backupCount > 0 && <Badge colorPalette="green">{backupCount} backup(s)</Badge>}
                    </HStack>
                    <HStack align="start" gap="2">
                        <Heading size="2xl" flex="1">{item.title ?? `#${item.original_id}`}</Heading>
                    </HStack>
                    {item.title_overridden && item.original?.title && (
                        <Text textStyle="hint">Original title: {item.original.title}</Text>
                    )}
                    {item.author && (
                        <HStack gap="2">
                            <Avatar.Root size="xs">
                                <Avatar.Fallback name={item.author.name}/>
                            </Avatar.Root>
                            <Text fontSize="sm">{item.author.name}</Text>
                        </HStack>
                    )}
                    <HStack gap="4" fontSize="sm" color="fg.muted" wrap="wrap">
                        {item.source_time_updated && <Text>Updated {formatRelative(item.source_time_updated)}</Text>}
                        {item.file_size !== null && <Text>{formatBytes(item.file_size)}</Text>}
                        <Text>Added {formatRelative(item.personal.time_registered)}</Text>
                        {!isLocal && <Text>Synced {formatRelative(item.time_synced)}</Text>}
                    </HStack>
                    <HStack gap="2" wrap="wrap" mt="auto">
                        {can("ITEMS_WRITE") && (
                            <Button size="sm" colorPalette="brand" onClick={onEdit}>
                                <FiEdit2/> Edit
                            </Button>
                        )}
                        {sourceUrl && (
                            <Button size="sm" variant="outline" asChild>
                                <a href={sourceUrl} target="_blank" rel="noopener noreferrer">
                                    <FiExternalLink/> Open in {modspaceLabels[item.modspace]}
                                </a>
                            </Button>
                        )}
                        {!isLocal && can("ITEMS_REGISTER") && (
                            <Button size="sm" variant="outline" loading={refresh.isPending}
                                    onClick={() => refresh.mutate(undefined, {onSuccess: () => notifySuccess("Metadata refreshed")})}>
                                <FiRefreshCw/> Refresh
                            </Button>
                        )}
                        {can("ITEMS_WRITE") && (
                            <Button size="sm" variant="subtle" colorPalette="red"
                                    onClick={onRemove}>
                                <FiTrash2/> Remove
                            </Button>
                        )}
                    </HStack>
                </Card.Body>
            </Flex>
        </Card.Root>
    );
};

export default ItemHeader;
