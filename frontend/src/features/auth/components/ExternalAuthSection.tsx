import {Button, Image, Text, VStack} from "@chakra-ui/react";
import type {AuthProvider} from "@/features/auth/types.ts";

const ExternalAuthSection = ({providers}: { providers: AuthProvider[] }) => {
    return (
        <VStack minW="full" gap="3" align="stretch">
            {providers.map(provider => (
                <AuthProviderButton key={provider.id} provider={provider}/>
            ))}
        </VStack>
    );
};
export default ExternalAuthSection;

const AuthProviderButton = ({provider}: { provider: AuthProvider }) => {
    const {mutate, isPending} = provider.redirectHook();

    return (
        <Button variant="surface" colorPalette="gray" aria-label={`Continue with ${provider.name}`}
                onClick={() => mutate()}
                loading={isPending} disabled={isPending}
        >
            <Image src={provider.icon} alt="" boxSize="24px" objectFit="contain"/>
            <Text whiteSpace="nowrap">
                Continue with {provider.name}
            </Text>
        </Button>
    );
};