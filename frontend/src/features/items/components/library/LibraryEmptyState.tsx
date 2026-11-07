import {Button, EmptyState, VStack} from "@chakra-ui/react";
import {FiArchive, FiPlus} from "react-icons/fi";

interface LibraryEmptyStateProps {
    hasQuery: boolean;
    canAdd: boolean;
    onAdd: () => void;
}

export const LibraryEmptyState = ({hasQuery, canAdd, onAdd}: LibraryEmptyStateProps) => (
    <EmptyState.Root>
        <EmptyState.Content>
            <EmptyState.Indicator><FiArchive/></EmptyState.Indicator>
            <VStack textAlign="center">
                <EmptyState.Title>
                    {hasQuery ? "Nothing matches your search" : "Your library is empty"}
                </EmptyState.Title>
                <EmptyState.Description>
                    {hasQuery ? "Try other words or reset the filters."
                        : canAdd ? "Add Steam Workshop items by link or id, or create local items."
                            : "Your account can't add items. Ask an administrator for access."}
                </EmptyState.Description>
            </VStack>
            {!hasQuery && canAdd && (
                <Button colorPalette="brand" onClick={onAdd}>
                    <FiPlus/> Add items
                </Button>
            )}
        </EmptyState.Content>
    </EmptyState.Root>
);

export default LibraryEmptyState;
