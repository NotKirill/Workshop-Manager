import {Badge, Box, Card, Checkbox, Flex, HStack, Image, LinkBox, LinkOverlay, Text, Wrap} from "@chakra-ui/react";
import {Link as RouterLink} from "react-router";
import {FiArchive, FiEdit2, FiHardDrive, FiLoader} from "react-icons/fi";
import {bbcodeToPlainText} from "@/lib/bbcodeParser.ts";
import {formatRelative} from "@/lib/format.ts";
import {RatingStars} from "@/features/shared/RatingStars.tsx";
import type {ItemSummary} from "@/features/items/types.ts";

interface ItemCardProps {
    item: ItemSummary;
    selectable: boolean;
    selected: boolean;
    onToggle: (id: number) => void;
}

export const ItemCard = ({item, selectable, selected, onToggle}: ItemCardProps) => {
    const snippet = bbcodeToPlainText(item.description_snippet);
    const title = item.title ?? `#${item.original_id}`;
    return (
        <LinkBox asChild>
            <Card.Root
                overflow="hidden"
                cursor="pointer"
                borderColor={selected ? "brand.solid" : undefined}
                outline={selected ? "1px solid" : undefined}
                outlineColor="brand.solid"
                transition="box-shadow 0.15s"
                _hover={{shadow: "md"}}
                css={{"&:has(a:focus-visible)": {outline: "2px solid", outlineColor: "brand.focusRing"}}}
                onClick={selectable ? () => onToggle(item.id) : undefined}
            >
                <Box position="relative" h="150px" bg="bg.muted">
                    {item.preview_url ? (
                        <Image src={item.preview_url} alt="" w="full" h="full" objectFit="cover" loading="lazy"/>
                    ) : (
                        <Flex h="full" align="center" justify="center" color="fg.subtle">
                            <FiArchive size={40}/>
                        </Flex>
                    )}
                    {selectable && (
                        <Box position="absolute" top="2" left="2" bg="bg.panel" borderRadius="sm" p="1"
                             onClick={event => event.stopPropagation()}>
                            <Checkbox.Root checked={selected} onCheckedChange={() => onToggle(item.id)}
                                           colorPalette="brand">
                                <Checkbox.HiddenInput/>
                                <Checkbox.Control/>
                            </Checkbox.Root>
                        </Box>
                    )}
                    <HStack position="absolute" top="2" right="2" gap="1">
                        {item.source_status === "UNAVAILABLE" && <Badge colorPalette="red">Removed</Badge>}
                        {item.backup?.active_job && (
                            <Badge colorPalette="blue"><FiLoader/> {item.backup.active_job === "BACKUP" ? "Backing up" : "Deleting"}</Badge>
                        )}
                        {(item.backup?.revision_count ?? 0) > 0 && (
                            <Badge colorPalette="green" title="Backups stored"><FiHardDrive/> {item.backup?.revision_count}</Badge>
                        )}
                    </HStack>
                </Box>
                <Card.Body gap="1.5" p="3">
                    <HStack gap="1" align="start">
                        <Text fontWeight="semibold" lineClamp={2} flex="1">
                            {selectable ? title : (
                                <LinkOverlay asChild _focusVisible={{outline: "none"}}>
                                    <RouterLink to={`/items/${item.id}`}>{title}</RouterLink>
                                </LinkOverlay>
                            )}
                        </Text>
                        {item.title_overridden && item.modspace !== "LOCAL" && (
                            <Box color="fg.muted" pt="1" title="Title edited by you"><FiEdit2 size={12}/></Box>
                        )}
                    </HStack>
                    <HStack gap="1" wrap="wrap">
                        <Badge variant="subtle">{item.game.name}</Badge>
                        {item.modspace === "LOCAL" && <Badge variant="outline">Local</Badge>}
                    </HStack>
                    {snippet && <Text textStyle="hint" lineClamp={2}>{snippet}</Text>}
                    <Flex justify="space-between" align="center" mt="auto" pt="1">
                        <RatingStars value={item.rating} size={12}/>
                        <Text fontSize="xs" color="fg.subtle" title="Last update of the original">
                            {item.source_time_updated ? formatRelative(item.source_time_updated) : ""}
                        </Text>
                    </Flex>
                    {item.tags.length > 0 && (
                        <Wrap gap="1">
                            {item.tags.map(tag => (
                                <Badge key={tag.id} size="sm" colorPalette="purple" variant="surface">{tag.name}</Badge>
                            ))}
                        </Wrap>
                    )}
                </Card.Body>
            </Card.Root>
        </LinkBox>
    );
};

export default ItemCard;
