import {Button, Image, Text, VStack} from "@chakra-ui/react";
import type {AuthProvider} from "@/features/auth/types.ts";

const ExternalAuthSection = ({providers}: { providers: AuthProvider[] }) => {
    return (
        <VStack minW="full" gap={3} align="stretch">
            {providers.map(provider => (
                <Button key={provider.id} variant="surface" colorPalette="gray">
                    <Image src={provider.icon} boxSize="24px" objectFit="contain"/>
                    <Text whiteSpace="nowrap">
                        Continue with {provider.name}
                    </Text>
                </Button>
            ))}
        </VStack>
    )
}
export default ExternalAuthSection
