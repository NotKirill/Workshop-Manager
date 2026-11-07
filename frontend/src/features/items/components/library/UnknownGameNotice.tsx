import {Alert, Button} from "@chakra-ui/react";

interface UnknownGameNoticeProps {
    count: number;
    showing: boolean;
    onToggle: () => void;
}

export const UnknownGameNotice = ({count, showing, onToggle}: UnknownGameNoticeProps) => (
    <Alert.Root status="info" size="sm">
        <Alert.Indicator/>
        <Alert.Content>
            <Alert.Title>{count} item(s) with unknown game</Alert.Title>
            <Alert.Description>
                Their source doesn't show them right now. They move to their game automatically once it does.
            </Alert.Description>
        </Alert.Content>
        <Button size="xs" variant="outline" alignSelf="center" onClick={onToggle}>
            {showing ? "Show all" : "Show"}
        </Button>
    </Alert.Root>
);

export default UnknownGameNotice;
