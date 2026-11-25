import {
    Badge,
    Box,
    Button,
    Card,
    Center,
    Container,
    Grid,
    Heading,
    HStack,
    Spinner,
    Stack,
    Tabs,
    Text,
} from "@chakra-ui/react";
import {useState} from "react";
import {Link as RouterLink, useNavigate, useParams} from "react-router";
import {FiArrowLeft} from "react-icons/fi";
import {useItem} from "@/features/items/queries.ts";
import {useUnregisterItem} from "@/features/items/mutations.ts";
import {BBCodeView} from "@/features/shared/BBCodeView.tsx";
import {formatDate} from "@/lib/format.ts";
import {getErrorMessage} from "@/lib/errors.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {ConfirmDialog} from "@/features/shared/ConfirmDialog.tsx";
import {ItemEditDialog} from "@/features/items/components/item/ItemEditDialog.tsx";
import {OrganizePanel} from "@/features/items/components/item/OrganizePanel.tsx";
import {RelationsPanel} from "@/features/items/components/item/RelationsPanel.tsx";
import {DependenciesPanel} from "@/features/items/components/item/DependenciesPanel.tsx";
import {ContentPanel} from "@/features/items/components/item/ContentPanel.tsx";
import {MediaPanel} from "@/features/items/components/item/MediaPanel.tsx";
import {ItemHeader} from "@/features/items/components/item/ItemHeader.tsx";
import {SteamStats} from "@/features/items/components/item/SteamStats.tsx";
import {ErrorAlert} from "@/features/shared/ErrorAlert.tsx";

export const ItemPage = () => {
    const {id} = useParams();
    const itemId = Number(id);
    const navigate = useNavigate();
    const [editing, setEditing] = useState(false);
    const [confirmRemove, setConfirmRemove] = useState(false);

    const {data: item, isLoading, error} = useItem(itemId);

    const remove = useUnregisterItem(itemId);

    if (isLoading) {
        return <Center py="24"><Spinner size="xl"/></Center>;
    }
    if (error || !item) {
        return (
            <Container maxW="3xl" py="10">
                <ErrorAlert title={getErrorMessage(error, "Item not found")}/>
                <Button mt="4" variant="outline" asChild><RouterLink to="/library"><FiArrowLeft/> Back</RouterLink></Button>
            </Container>
        );
    }

    const isLocal = item.modspace === "LOCAL";
    const backupCount = item.content.summary.revisions.length;
    const mediaCount = item.media.filter(media => !media.hidden).length;

    return (
        <Container maxW="7xl" py="6">
            <Stack gap="6">
                <HStack>
                    <Button size="sm" variant="ghost" asChild>
                        <RouterLink to="/library"><FiArrowLeft/> Library</RouterLink>
                    </Button>
                </HStack>

                <ItemHeader item={item} onEdit={() => setEditing(true)} onRemove={() => setConfirmRemove(true)}/>

                <Tabs.Root defaultValue="overview" variant="line" lazyMount>
                    <Tabs.List>
                        <Tabs.Trigger value="overview">Overview</Tabs.Trigger>
                        <Tabs.Trigger value="backups">Backups {backupCount > 0 && `(${backupCount})`}</Tabs.Trigger>
                        <Tabs.Trigger value="media">Media {mediaCount > 0 && `(${mediaCount})`}</Tabs.Trigger>
                    </Tabs.List>
                    <Tabs.Content value="overview">
                        <Grid templateColumns={{base: "1fr", lg: "2fr 1fr"}} gap="6">
                            <Stack gap="6" minW="0">
                                {item.personal.user_commentary && (
                                    <Card.Root variant="subtle">
                                        <Card.Body>
                                            <Text textStyle="sectionTitle" mb="1">Your notes</Text>
                                            <Text whiteSpace="pre-wrap">{item.personal.user_commentary}</Text>
                                        </Card.Body>
                                    </Card.Root>
                                )}
                                <Box>
                                    <HStack mb="2" gap="2">
                                        <Heading size="md">Description</Heading>
                                        {item.description_overridden && !isLocal && <Badge size="sm">edited by you</Badge>}
                                    </HStack>
                                    <BBCodeView text={item.description}/>
                                </Box>
                                <Stack gap="4">
                                    <RelationsPanel item={item}/>
                                    <DependenciesPanel item={item}/>
                                </Stack>
                            </Stack>
                            <Stack gap="6">
                                <Card.Root>
                                    <Card.Body><OrganizePanel item={item}/></Card.Body>
                                </Card.Root>
                                {item.steam && (
                                    <Card.Root>
                                        <Card.Body>
                                            <Heading size="sm" mb="3">Steam Workshop</Heading>
                                            <SteamStats item={item}/>
                                            <Text fontSize="xs" color="fg.muted" mt="3">
                                                Created {formatDate(item.source_time_created)}
                                            </Text>
                                        </Card.Body>
                                    </Card.Root>
                                )}
                            </Stack>
                        </Grid>
                    </Tabs.Content>
                    <Tabs.Content value="backups">
                        <ContentPanel item={item}/>
                    </Tabs.Content>
                    <Tabs.Content value="media">
                        <MediaPanel item={item}/>
                    </Tabs.Content>
                </Tabs.Root>
            </Stack>

            {editing && <ItemEditDialog item={item} open={editing} onOpenChange={setEditing}/>}
            <ConfirmDialog open={confirmRemove} onOpenChange={setConfirmRemove} destructive
                           title="Remove from library?" confirmLabel="Remove" loading={remove.isPending}
                           onConfirm={() => remove.mutate(undefined, {
                               onSuccess: () => {
                                   notifySuccess("Removed from your library");
                                   void navigate("/library", {replace: true});
                               },
                           })}>
                Your notes, tags, groups, rating and media of this item will be removed. Shared metadata and backups
                stay for other users (and for you, if you add the item again soon).
            </ConfirmDialog>
        </Container>
    );
};

export default ItemPage;
