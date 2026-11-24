import {Center, HStack, List, Text} from "@chakra-ui/react";
import {FiCheck, FiCircle} from "react-icons/fi";
import {PASSWORD_RULES} from "@/features/auth/schemas.ts";

export const PasswordRules = ({password}: { password: string }) => (
    <List.Root variant="plain" gap="0.5" fontSize="sm" aria-live="polite">
        {PASSWORD_RULES.map(rule => {
            const met = rule.test(password);
            return (
                <List.Item key={rule.label}>
                    <HStack gap="1.5" color={met ? "fg.success" : "fg.muted"}>
                        {/* Same width for both icons, so the labels don't move when a rule is met */}
                        <Center w="4" flexShrink={0}>
                            {met ? <FiCheck aria-label="met"/> : <FiCircle size={8} aria-label="not met yet"/>}
                        </Center>
                        <Text as="span">{rule.label}</Text>
                    </HStack>
                </List.Item>
            );
        })}
    </List.Root>
);

export default PasswordRules;
