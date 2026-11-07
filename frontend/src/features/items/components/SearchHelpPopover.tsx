import {Badge, Box, Code, IconButton, Popover, Portal, Stack, Text, Wrap} from "@chakra-ui/react";
import {FiHelpCircle} from "react-icons/fi";
import {useSearchHelp} from "@/features/items/queries.ts";

export const SearchHelpPopover = ({onExample}: { onExample: (query: string) => void }) => {
    const {data} = useSearchHelp();
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
                                <Box>
                                    <Text fontWeight="semibold" mb="1">Fields</Text>
                                    <Wrap gap="1">
                                        {data?.fields.map(field => (
                                            <Badge key={field.tag} variant="surface"
                                                   title={field.values.length > 0 ? field.values.join(", ") : field.type}>
                                                {field.tag}{field.sortable ? " ↕" : ""}
                                            </Badge>
                                        ))}
                                    </Wrap>
                                </Box>
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
