import {AspectRatio, Badge, Box, Button, chakra, Grid, HStack, IconButton, Image, Input, Stack, Text} from "@chakra-ui/react";
import {type ReactNode, useRef, useState} from "react";
import {FiEye, FiEyeOff, FiImage, FiLink, FiStar, FiTrash2} from "react-icons/fi";
import {
    useAddEmbed,
    useAddMediaLink,
    useDeleteMedia,
    useSetMediaHidden,
    useUploadMedia
} from "@/features/items/mutations.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {usePermissions} from "@/features/auth/queries.ts";
import {MEDIA_PURPOSES} from "@/features/items/mediaPurposes.ts";
import type {ItemDetail, MediaEntry} from "@/features/items/types.ts";

const Embed = ({url, title}: { url: string; title: string }) => (
    <AspectRatio ratio={16 / 9}>
        <chakra.iframe src={url} title={title} allowFullScreen border="0" borderRadius="lg"/>
    </AspectRatio>
);

const MediaTile = ({media, actions}: { media: MediaEntry; actions?: ReactNode }) => (
    <Box position="relative" opacity={media.hidden ? 0.5 : 1}>
        {media.type === "FILE" && media.url ? (
            <a href={media.url} target="_blank" rel="noopener noreferrer">
                <Image src={media.url} alt={media.file_name ?? ""} borderRadius="md" w="full" h="160px"
                       objectFit="cover"/>
            </a>
        ) : media.url ? (
            <Embed url={media.url} title={media.provider ?? "embed"}/>
        ) : null}
        <Badge position="absolute" top="2" left="2" variant="solid" colorPalette={media.owned ? "blue" : "gray"}>
            {media.owned ? "Yours" : "Source"}
        </Badge>
        {actions && <HStack position="absolute" top="2" right="2" gap="1">{actions}</HStack>}
    </Box>
);

const MediaGrid = ({children}: { children: ReactNode }) => (
    <Grid templateColumns="repeat(auto-fill, minmax(260px, 1fr))" gap="3">{children}</Grid>
);

export const MediaPanel = ({item}: { item: ItemDetail }) => {
    const fileInput = useRef<HTMLInputElement>(null);
    const [embedUrl, setEmbedUrl] = useState("");
    const can = usePermissions();
    const canWrite = can("ITEMS_WRITE");

    const upload = useUploadMedia(item.id);
    const embed = useAddEmbed(item.id);
    const makePreview = useAddMediaLink(item.id);
    const setHidden = useSetMediaHidden(item.id);
    const remove = useDeleteMedia(item.id);

    const preview = item.media.find(media => media.purpose === MEDIA_PURPOSES.PREVIEW && !media.hidden);
    const showcase = item.media.filter(media => media.purpose === MEDIA_PURPOSES.SHOWCASE && !media.hidden);
    const hidden = item.media.filter(media => media.hidden);

    // Own links are removed; source links are only hidden for this user
    const removeOrHide = (media: MediaEntry, label: string) => media.owned ? (
        <IconButton aria-label={label} size="xs" colorPalette="red" variant="solid"
                    onClick={() => remove.mutate(media.id)}>
            <FiTrash2/>
        </IconButton>
    ) : (
        <IconButton aria-label="Hide for me" size="xs" variant="solid"
                    onClick={() => setHidden.mutate({linkId: media.id, hidden: true})}>
            <FiEyeOff/>
        </IconButton>
    );

    return (
        <Stack gap="6">
            <Box>
                <Text fontWeight="semibold" mb="2">Preview</Text>
                {preview ? (
                    <MediaGrid>
                        <MediaTile media={preview}
                                   actions={canWrite && removeOrHide(preview, "Remove my preview")}/>
                    </MediaGrid>
                ) : (
                    <Text textStyle="hint">No preview. Use an image of the showcase as preview.</Text>
                )}
            </Box>

            <Box>
                <HStack justify="space-between" mb="2" wrap="wrap" gap="2">
                    <Text fontWeight="semibold">Showcase</Text>
                    {can("MEDIA_UPLOAD") && (
                        <HStack>
                            <Input ref={fileInput} type="file" display="none"
                                   accept="image/png,image/jpeg,image/gif,image/webp,image/avif" onChange={event => {
                                const file = event.target.files?.[0];
                                if (file) upload.mutate({file, purpose: MEDIA_PURPOSES.SHOWCASE},
                                    {onSuccess: () => notifySuccess("Image added")});
                                event.target.value = "";
                            }}/>
                            <Button size="xs" variant="outline" loading={upload.isPending}
                                    onClick={() => fileInput.current?.click()}>
                                <FiImage/> Upload image
                            </Button>
                        </HStack>
                    )}
                </HStack>
                {canWrite && <HStack mb="3">
                    <Input size="sm" placeholder="YouTube or Sketchfab link" value={embedUrl}
                           onChange={event => setEmbedUrl(event.target.value)}/>
                    <Button size="sm" variant="outline" disabled={!embedUrl.trim()} loading={embed.isPending}
                            onClick={() => embed.mutate({url: embedUrl.trim(), purpose: MEDIA_PURPOSES.SHOWCASE},
                                {onSuccess: () => setEmbedUrl("")})}>
                        <FiLink/> Embed
                    </Button>
                </HStack>}
                {showcase.length === 0 && <Text textStyle="hint">No media added yet.</Text>}
                <MediaGrid>
                    {showcase.map(media => (
                        <MediaTile key={media.id} media={media} actions={canWrite && <>
                            {media.type === "FILE" && preview?.media_item_id !== media.media_item_id && (
                                <IconButton aria-label="Use as preview" size="xs" variant="solid"
                                            loading={makePreview.isPending
                                                && makePreview.variables.mediaItemId === media.media_item_id}
                                            onClick={() => makePreview.mutate(
                                                {mediaItemId: media.media_item_id, purpose: MEDIA_PURPOSES.PREVIEW})}>
                                    <FiStar/>
                                </IconButton>
                            )}
                            {removeOrHide(media, "Remove media")}
                        </>}/>
                    ))}
                </MediaGrid>
            </Box>

            {hidden.length > 0 && (
                <Box>
                    <Text fontWeight="semibold" mb="2">Hidden for me</Text>
                    <MediaGrid>
                        {hidden.map(media => (
                            <MediaTile key={media.id} media={media} actions={canWrite && (
                                <IconButton aria-label="Show again" size="xs" variant="solid"
                                            onClick={() => setHidden.mutate({linkId: media.id, hidden: false})}>
                                    <FiEye/>
                                </IconButton>
                            )}/>
                        ))}
                    </MediaGrid>
                </Box>
            )}
        </Stack>
    );
};

export default MediaPanel;
