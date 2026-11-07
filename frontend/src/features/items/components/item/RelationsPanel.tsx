import {Badge, Box, Button, HStack, IconButton, Link, Stack, Text} from "@chakra-ui/react";
import {useState} from "react";
import {Link as RouterLink} from "react-router";
import {FiPlus, FiX} from "react-icons/fi";
import {useRemoveRelation} from "@/features/items/mutations.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import type {ItemDetail, RelationType} from "@/features/items/types.ts";
import {AddRelationDialog} from "@/features/items/components/item/AddRelationDialog.tsx";

const relationLabels: Record<RelationType, [string, string]> = {
    DEPENDENCY: ["Requires", "Required by"],
    PARENT: ["Parent of", "Child of"],
    ALTERNATE: ["Alternative to", "Alternative to"],
};

export const RelationsPanel = ({item}: { item: ItemDetail }) => {
    const [adding, setAdding] = useState(false);
    const can = usePermissions();
    const remove = useRemoveRelation(item.id);

    return (
        <Box>
            <HStack justify="space-between" mb="2">
                <Text fontWeight="semibold">Relations</Text>
                {can("ITEMS_WRITE") && (
                    <Button size="xs" variant="outline" onClick={() => setAdding(true)}><FiPlus/> Add</Button>
                )}
            </HStack>
            {item.relations.length === 0 && <Text textStyle="hint">No relations yet.</Text>}
            <Stack gap="1">
                {item.relations.map(relation => {
                    const outgoing = relation.direction === "OUTGOING";
                    return (
                        <HStack key={`${relation.direction}-${relation.item_id}-${relation.relation}`} gap="2">
                            <Badge variant="surface">{relationLabels[relation.relation][outgoing ? 0 : 1]}</Badge>
                            <Link asChild color="brand.fg" fontSize="sm">
                                <RouterLink to={`/items/${relation.item_id}`}>{relation.title ?? `#${relation.item_id}`}</RouterLink>
                            </Link>
                            {outgoing && can("ITEMS_WRITE") && (
                                <IconButton aria-label="Remove relation" size="2xs" variant="ghost"
                                            onClick={() => remove.mutate({targetId: relation.item_id, relation: relation.relation})}>
                                    <FiX/>
                                </IconButton>
                            )}
                        </HStack>
                    );
                })}
            </Stack>
            <AddRelationDialog item={item} open={adding} onOpenChange={setAdding}/>
        </Box>
    );
};

export default RelationsPanel;
