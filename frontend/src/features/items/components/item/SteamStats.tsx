import {Badge, DataList, Link} from "@chakra-ui/react";
import {formatNumber} from "@/lib/format.ts";
import type {ItemDetail} from "@/features/items/types.ts";

const visibilityLabels: Record<number, string> = {0: "Public", 1: "Friends only", 2: "Private", 3: "Unlisted"};

export const SteamStats = ({item}: { item: ItemDetail }) => {
    const steam = item.steam;
    if (!steam) return null;
    return (
        <DataList.Root orientation="horizontal" size="sm">
            <DataList.Item>
                <DataList.ItemLabel>Subscribers</DataList.ItemLabel>
                <DataList.ItemValue>{formatNumber(steam.subscriptions)} (lifetime {formatNumber(steam.lifetime_subscriptions)})</DataList.ItemValue>
            </DataList.Item>
            <DataList.Item>
                <DataList.ItemLabel>Favorites</DataList.ItemLabel>
                <DataList.ItemValue>{formatNumber(steam.favorited)}</DataList.ItemValue>
            </DataList.Item>
            {steam.votes_up !== null && (
                <DataList.Item>
                    <DataList.ItemLabel>Votes</DataList.ItemLabel>
                    <DataList.ItemValue>
                        👍 {formatNumber(steam.votes_up)} · 👎 {formatNumber(steam.votes_down)}
                        {steam.score !== null && ` (${Math.round(steam.score * 100)}%)`}
                    </DataList.ItemValue>
                </DataList.Item>
            )}
            <DataList.Item>
                <DataList.ItemLabel>Visibility</DataList.ItemLabel>
                <DataList.ItemValue>{visibilityLabels[steam.visibility] ?? steam.visibility}</DataList.ItemValue>
            </DataList.Item>
            {(steam.maybe_inappropriate_sex || steam.maybe_inappropriate_violence) && (
                <DataList.Item>
                    <DataList.ItemLabel>Content</DataList.ItemLabel>
                    <DataList.ItemValue>
                        <Badge colorPalette="orange">Mature content</Badge>
                    </DataList.ItemValue>
                </DataList.Item>
            )}
            {steam.links.map(link => (
                <DataList.Item key={link.key}>
                    <DataList.ItemLabel>{link.key}</DataList.ItemLabel>
                    <DataList.ItemValue>
                        {/^https?:\/\//.test(link.value) ? (
                            <Link href={link.value} target="_blank" rel="noopener noreferrer nofollow"
                                  color="brand.fg">{link.value}</Link>
                        ) : link.value}
                    </DataList.ItemValue>
                </DataList.Item>
            ))}
        </DataList.Root>
    );
};

export default SteamStats;
