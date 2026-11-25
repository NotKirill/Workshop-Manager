import {Box, chakra, Code, Heading, Image, Link, List, Separator, Table, Text} from "@chakra-ui/react";
import {type ReactNode, useState} from "react";
import {type BBNode, parse, plainText} from "@/lib/bbcodeParser.ts";

const toSafeUrl = (url: string | null): string | null => {
    const trimmed = url?.trim();
    if (!trimmed) return null;
    const hasScheme = /^[a-z][a-z0-9+.-]*:/i.test(trimmed);
    try {
        const parsed = new URL(hasScheme ? trimmed : `https://${trimmed.replace(/^\/\//, "")}`);
        if (parsed.protocol !== "http:" && parsed.protocol !== "https:") return null;
        return hasScheme || parsed.hostname.includes(".") ? parsed.href : null;
    } catch {
        return null;
    }
};

const Spoiler = ({children}: { children: ReactNode }) => {
    const [revealed, setRevealed] = useState(false);
    return (
        <Box as="span" bg={revealed ? "transparent" : "fg"} color={revealed ? "inherit" : "transparent"}
             cursor={revealed ? undefined : "pointer"} borderRadius="sm" px="1"
             role={revealed ? undefined : "button"} tabIndex={revealed ? undefined : 0}
             aria-label={revealed ? undefined : "Spoiler, press to reveal"}
             onClick={() => setRevealed(true)}
             onKeyDown={event => {
                 if (!revealed && (event.key === "Enter" || event.key === " ")) {
                     event.preventDefault();
                     setRevealed(true);
                 }
             }}>
            {children}
        </Box>
    );
};

const renderText = (value: string, key: string): ReactNode => {
    const lines = value.split("\n");
    return lines.map((line, index) => (
        <span key={`${key}-${index}`}>
            {line}
            {index < lines.length - 1 && <br/>}
        </span>
    ));
};

const renderNodes = (nodes: BBNode[], keyPrefix: string): ReactNode[] =>
    nodes.map((node, index) => renderNode(node, `${keyPrefix}-${index}`));

const renderNode = (node: BBNode, key: string): ReactNode => {
    if (node.kind === "text") {
        return renderText(node.value, key);
    }
    const children = renderNodes(node.children, key);
    switch (node.name) {
        case "h1":
            return <Heading key={key} as="h3" size="xl" mt="4" mb="2">{children}</Heading>;
        case "h2":
            return <Heading key={key} as="h4" size="lg" mt="3" mb="2">{children}</Heading>;
        case "h3":
            return <Heading key={key} as="h5" size="md" mt="3" mb="1">{children}</Heading>;
        case "b":
            return <strong key={key}>{children}</strong>;
        case "u":
            return <u key={key}>{children}</u>;
        case "i":
            return <em key={key}>{children}</em>;
        case "strike":
            return <s key={key}>{children}</s>;
        case "spoiler":
            return <Spoiler key={key}>{children}</Spoiler>;
        case "noparse":
            return <span key={key}>{plainText(node.children)}</span>;
        case "hr":
            return <Separator key={key} my="3"/>;
        case "p":
            return <Text key={key} mb="2">{children}</Text>;
        case "url": {
            const text = node.arg ?? plainText(node.children);
            const href = toSafeUrl(text);
            if (!href) return <span key={key}>{children}</span>;
            return (
                <Link key={key} href={href} target="_blank" rel="noopener noreferrer nofollow"
                      display="inline" color="brand.fg" textDecoration="underline">
                    {children.length > 0 ? children : text}
                </Link>
            );
        }
        case "img": {
            const src = toSafeUrl(node.arg ?? plainText(node.children));
            if (!src) return null;
            return <Image key={key} src={src} alt="" maxW="100%" my="2" borderRadius="md" loading="lazy"/>;
        }
        case "previewyoutube": {
            const id = (node.arg ?? "").split(";")[0];
            if (!/^[A-Za-z0-9_-]{11}$/.test(id)) return null;
            return (
                <Box key={key} my="3" maxW="640px" aspectRatio={16 / 9}>
                    <chakra.iframe title="YouTube" src={`https://www.youtube-nocookie.com/embed/${id}`}
                                   w="full" h="full" border="0" allowFullScreen/>
                </Box>
            );
        }
        case "list":
            return <List.Root key={key} ps="5" my="2">{children}</List.Root>;
        case "olist":
            return <List.Root key={key} as="ol" ps="5" my="2" listStyleType="decimal">{children}</List.Root>;
        case "*":
            return <List.Item key={key}>{children}</List.Item>;
        case "quote":
            return (
                <Box key={key} borderStartWidth="3px" borderColor="border.emphasized" ps="3" my="2"
                     color="fg.muted">
                    {node.arg && <Text textStyle="sectionTitle">{node.arg} wrote:</Text>}
                    {children}
                </Box>
            );
        case "code":
            return (
                <Code key={key} display="block" whiteSpace="pre-wrap" p="2" my="2">
                    {plainText(node.children)}
                </Code>
            );
        case "table":
            return (
                <Table.ScrollArea key={key} maxW="full">
                    <Table.Root my="2">
                        <Table.Body>{children}</Table.Body>
                    </Table.Root>
                </Table.ScrollArea>
            );
        case "tr":
            return <Table.Row key={key}>{children}</Table.Row>;
        case "th":
            return <Table.ColumnHeader key={key}>{children}</Table.ColumnHeader>;
        case "td":
            return <Table.Cell key={key}>{children}</Table.Cell>;
        default:
            return <span key={key}>{children}</span>;
    }
};

export const BBCodeView = ({text}: { text: string | null | undefined }) => {
    if (!text) {
        return <Text color="fg.muted">No description</Text>;
    }
    return <Box lineHeight="tall" wordBreak="break-word">{renderNodes(parse(text), "bb")}</Box>;
};

export default BBCodeView;
