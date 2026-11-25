import {Badge, Box, Code, IconButton, Popover, Portal, Stack, Text, Wrap} from "@chakra-ui/react";
import {FiHelpCircle} from "react-icons/fi";
import {useSearchHelp} from "@/features/items/queries.ts";
import {modspaceLabels} from "@/lib/format.ts";
import type {ModspaceType} from "@/lib/apiTypes.ts";
import type {SearchHelpField} from "@/features/items/types.ts";

const groupOf = (tag: string): string => {
    const prefix = tag.includes(".") ? tag.split(".")[0] : null;
    if (!prefix) return "Fields";
    return modspaceLabels[prefix.toUpperCase() as ModspaceType] ?? prefix;
};

export const SearchHelpPopover = ({onExample}: { onExample: (query: string) => void }) => {
    const {data} = useSearchHelp();
    const groups = new Map<string, SearchHelpField[]>();
    for (const field of data?.fields ?? []) {
        const group = groupOf(field.tag);
        groups.set(group, [...(groups.get(group) ?? []), field]);
    }
    return (
        <Popover.Root positioning={{placement: "bottom-end"}}>
            <Popover.Trigger asChild>
                <IconButton aria-label="Search help" variant="ghost"><FiHelpCircle/></IconButton>
            </Popover.Trigger>
            <Portal>
                <Popover.Positioner>
                    <Popover.Content maxW="md">
                        <Popover.Arrow/>
                        <Popover.Body>
                            <Stack gap="3" fontSize="sm">
                                <Text>
                                    Plain words search titles and descriptions. Narrow down with <Code>tag:value</Code>,
                                    combine with <Code>AND</Code> / <Code>OR</Code> / <Code>-</Code> (not) and
                                    parentheses. Numbers and dates support <Code>&gt;</Code> <Code>&lt;=</Code> and
                                    ranges <Code>a..b</Code>; dates also <Code>day</Code>, <Code>week</Code>,
                                    <Code>3_days_ago</Code>. Use <Code>order:field_asc</Code> and <Code>size:50</Code>.
                                </Text>
                                {[...groups].map(([group, fields]) => (
                                    <Box key={group}>
                                        <Text fontWeight="semibold" mb="1">{group}</Text>
                                        <Wrap gap="1">
                                            {fields.map(field => (
                                                <Badge key={field.tag} variant="surface"
                                                       title={field.values.length > 0 ? field.values.join(", ") : field.type}>
                                                    {field.tag}{field.sortable ? " ↕" : ""}
                                                </Badge>
                                            ))}
                                        </Wrap>
                                    </Box>
                                ))}
                                <Box>
                                    <Text fontWeight="semibold" mb="1">Examples (click to try)</Text>
                                    <Stack gap="1">
                                        {data?.examples.map(example => (
                                            <Code key={example} cursor="pointer" onClick={() => onExample(example)}>
                                                {example}
                                            </Code>
                                        ))}
                                    </Stack>
                                </Box>
                            </Stack>
                        </Popover.Body>
                    </Popover.Content>
                </Popover.Positioner>
            </Portal>
        </Popover.Root>
    );
};

export default SearchHelpPopover;
