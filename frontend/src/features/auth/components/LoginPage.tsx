import {Center, Heading, HStack, Separator, Stack, Text, VStack} from "@chakra-ui/react";
import {AuthenticationSection} from "@/features/auth/components/AuthenticationSection.tsx";
import {providers} from "@/features/auth/constants.ts";
import ExternalAuthSection from "@/features/auth/components/ExternalAuthSection.tsx";

const LoginPage = () => {
    // if (isPending) return <p>Loading...</p>;

    return (
        <Center h="100vh">
            <Stack boxShadow="md" maxW="md" w="full" p="12" rounded="md">
                <VStack gap={3}>
                    <VStack minW="full" align="stretch" textAlign="left">
                        <Heading>Sign in [to title?]</Heading>
                        <Text>Please sign in with [something-something]</Text>
                    </VStack>

                    <AuthenticationSection/>

                    <HStack minW="full">
                        <Separator flex="1"/>
                        <Text>or</Text>
                        <Separator flex="1"/>
                    </HStack>

                    <ExternalAuthSection providers={providers}/>
                </VStack>
            </Stack>
        </Center>
    );
};

export default LoginPage;