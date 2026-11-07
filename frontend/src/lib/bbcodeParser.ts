// Long TODO: This parser is slightly different than original. Unknown corner-cases which needs tests + table tags is not working right now

export interface TextNode {
    kind: "text";
    value: string;
}

export interface TagNode {
    kind: "tag";
    name: string;
    arg: string | null;
    children: BBNode[];
}

export type BBNode = TextNode | TagNode;

const KNOWN_TAGS = new Set([
    "h1", "h2", "h3", "b", "u", "i", "strike", "spoiler", "noparse", "hr", "url", "list", "olist", "*",
    "quote", "code", "table", "tr", "th", "td", "img", "previewyoutube", "p",
]);
const SELF_CLOSING = new Set(["hr", "*"]);
const TAG_PATTERN = /\[(\/?)([a-z0-9*]+)(?:=([^\]]*))?]/gi;

export const parse = (input: string): BBNode[] => {
    const root: TagNode = {kind: "tag", name: "root", arg: null, children: []};
    const stack: TagNode[] = [root];
    let lastIndex = 0;
    let match: RegExpExecArray | null;
    TAG_PATTERN.lastIndex = 0;

    const pushText = (value: string) => {
        if (value) stack[stack.length - 1].children.push({kind: "text", value});
    };

    while ((match = TAG_PATTERN.exec(input)) !== null) {
        const [raw, closing, rawName, arg] = match;
        const name = rawName.toLowerCase();
        const current = stack[stack.length - 1];

        if (current.name === "noparse" && !(closing && name === "noparse")) {
            continue;
        }
        if (!KNOWN_TAGS.has(name)) {
            continue;
        }
        pushText(input.slice(lastIndex, match.index));
        lastIndex = match.index + raw.length;

        if (closing) {
            const openIndex = stack.map(n => n.name).lastIndexOf(name);
            if (openIndex > 0) {
                stack.length = openIndex;
            } else {
                pushText(raw);
            }
            continue;
        }
        if (name === "*") {
            if (current.name === "*") stack.pop();
            const item: TagNode = {kind: "tag", name, arg: null, children: []};
            stack[stack.length - 1].children.push(item);
            stack.push(item);
            continue;
        }
        const node: TagNode = {kind: "tag", name, arg: arg ?? null, children: []};
        stack[stack.length - 1].children.push(node);
        if (!SELF_CLOSING.has(name)) stack.push(node);
    }
    pushText(input.slice(lastIndex));
    return root.children;
};

const BLOCK_TAGS = new Set(["h1", "h2", "h3", "hr", "list", "olist", "*", "quote", "table", "tr", "th", "td", "p"]);

export const plainText = (nodes: BBNode[]): string =>
    nodes.map(node => {
        if (node.kind === "text") return node.value;
        const text = plainText(node.children);
        return BLOCK_TAGS.has(node.name) ? ` ${text} ` : text;
    }).join("");


export const bbcodeToPlainText = (text: string | null | undefined): string =>
    text ? plainText(parse(text)).replace(/\s+/g, " ").trim() : "";
