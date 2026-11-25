import {IconButton, Input, InputGroup} from "@chakra-ui/react";
import {useState} from "react";
import {FiSearch, FiX} from "react-icons/fi";
import {SearchHelpPopover} from "@/features/items/components/SearchHelpPopover.tsx";

interface LibrarySearchBoxProps {
    initialValue: string;
    onSubmit: (value: string) => void;
}

export const LibrarySearchBox = ({initialValue, onSubmit}: LibrarySearchBoxProps) => {
    const [text, setText] = useState(initialValue);

    const submit = (value: string) => {
        setText(value);
        onSubmit(value);
    };

    return (
        <>
            <InputGroup flex="1" startElement={<FiSearch/>}
                        endElement={text ? (
                            <IconButton aria-label="Clear" size="xs" variant="ghost" onClick={() => submit("")}>
                                <FiX/>
                            </IconButton>
                        ) : undefined}>
                <Input placeholder='Search… e.g. map tag:favorite rating:>=8'
                       value={text}
                       onChange={event => setText(event.target.value)}
                       onKeyDown={event => {
                           if (event.key === "Enter") onSubmit(text);
                       }}/>
            </InputGroup>
            <SearchHelpPopover onExample={submit}/>
        </>
    );
};

export default LibrarySearchBox;
