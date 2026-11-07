import {Box, Center, Heading, HStack, Separator, Stack, Text, VStack} from "@chakra-ui/react";
import {useSearchParams} from "react-router";
import {FiArchive} from "react-icons/fi";
import {AuthenticationSection} from "@/features/auth/components/AuthenticationSection.tsx";
import {providers} from "@/features/auth/constants.ts";
import ExternalAuthSection from "@/features/auth/components/ExternalAuthSection.tsx";
import {ErrorAlert} from "@/features/shared/ErrorAlert.tsx";
import {redirectErrorMessage} from "@/features/auth/redirectErrors.ts";

const LoginPage = () => {
    const [searchParams] = useSearchParams();
    const error = searchParams.get("error");

    return (
        <Center minH="100vh" bg="bg.subtle" p="4">
            <Stack boxShadow="md" maxW="md" w="full" p={{base: "6", md: "12"}} rounded="lg" bg="bg.panel">
                <VStack gap="4">
                    <VStack minW="full" align="stretch" textAlign="left" gap="1">
                        <HStack color="brand.fg"><FiArchive size={26}/><Heading>Workshop Manager</Heading></HStack>
                        <Text color="fg.muted">Sign in to your mod library. Accounts are created by an administrator.</Text>
                    </VStack>

                    {error && (
                        <ErrorAlert description={redirectErrorMessage(error, "Sign-in failed.")}/>
                    )}

                    <AuthenticationSection/>

                    <HStack minW="full">
                        <Separator flex="1"/>
                        <Text color="fg.muted">or</Text>
                        <Separator flex="1"/>
                    </HStack>

                    <Box minW="full">
                        <ExternalAuthSection providers={providers}/>
                    </Box>
                </VStack>
            </Stack>
        </Center>
    );
};

export default LoginPage;
