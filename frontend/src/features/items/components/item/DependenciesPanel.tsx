import {Alert, Badge, Box, Button, HStack, Link, Stack, Text} from "@chakra-ui/react";
import {useState} from "react";
import {Link as RouterLink} from "react-router";
import {FiExternalLink} from "react-icons/fi";
import {useRegisterDependencies} from "@/features/items/mutations.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {itemUrl} from "@/features/items/modspaceReferences.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import type {ItemDetail} from "@/features/items/types.ts";
import {ReviewDependenciesDialog} from "@/features/items/components/item/ReviewDependenciesDialog.tsx";

export const DependenciesPanel = ({item}: { item: ItemDetail }) => {
    const [reviewing, setReviewing] = useState(false);
    const can = usePermissions();
    const tracked = item.personal.dependencies_need_review !== null;
    const needsReview = item.personal.dependencies_need_review === true;
    const registerDependencies = useRegisterDependencies(item.id);

    if (item.dependencies.length === 0 && !needsReview) return null;
    return (
        <Box>
            <HStack justify="space-between" mb="2">
                <Text fontWeight="semibold">Required items (declared by the author)</Text>
                {!tracked && item.dependencies.length > 0 && can("ITEMS_REGISTER") && (
                    <Button size="xs" colorPalette="brand" loading={registerDependencies.isPending}
                            onClick={() => registerDependencies.mutate(undefined, {
                                onSuccess: result => {
                                    const added = result.results.filter(r => r.status === "REGISTERED").length;
                                    notifySuccess(
                                        `${added} dependency item(s) added; changes by the source will be shown here`);
                                },
                            })}>
                        Add dependencies
                    </Button>
                )}
            </HStack>
            {needsReview && (
                <Alert.Root status="warning" size="sm" mb="2">
                    <Alert.Indicator/>
                    <Alert.Content>
                        <Alert.Description>The source changed its dependencies since you last reviewed them.</Alert.Description>
                    </Alert.Content>
                    {can("ITEMS_REGISTER") && (
                        <Button size="xs" variant="outline" alignSelf="center" onClick={() => setReviewing(true)}>
                            Review
                        </Button>
                    )}
                </Alert.Root>
            )}
            {item.dependencies.length === 0 && (
                <Text textStyle="hint">The source declares no dependencies anymore.</Text>
            )}
            <Stack gap="1">
                {item.dependencies.map(dep => {
                    const url = itemUrl(item.modspace, dep.original_id);
                    return (
                        <HStack key={dep.original_id} gap="2" fontSize="sm">
                            {dep.personal_id ? (
                                <Link asChild color="brand.fg">
                                    <RouterLink to={`/items/${dep.personal_id}`}>{dep.title ?? dep.original_id}</RouterLink>
                                </Link>
                            ) : (
                                <Text>{dep.title ?? dep.original_id}</Text>
                            )}
                            {!dep.personal_id && <Badge colorPalette="orange" size="sm">not in library</Badge>}
                            {url && (
                                <Link href={url} target="_blank" rel="noopener noreferrer" color="fg.muted">
                                    <FiExternalLink/>
                                </Link>
                            )}
                        </HStack>
                    );
                })}
            </Stack>
            <ReviewDependenciesDialog item={item} open={reviewing} onOpenChange={setReviewing}/>
        </Box>
    );
};

export default DependenciesPanel;
