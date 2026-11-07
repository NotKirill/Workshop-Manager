export const quoteSearchValue = (value: string) =>
    (/^-|[\s:()"]/.test(value) ? `"${value.replace(/"/g, '\\"')}"` : value);
