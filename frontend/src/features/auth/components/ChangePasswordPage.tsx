import {Button, Center, Heading, HStack, Input, Link as ChakraLink, Stack, Text, VStack} from "@chakra-ui/react";
import {z} from "zod";
import {useForm, useWatch} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {Link as RouterLink, useNavigate} from "react-router";
import {FiArchive} from "react-icons/fi";
import {loginSchema, passwordSignInSchema, passwordSignUpSchema} from "@/features/auth/schemas.ts";
import {useChangePasswordBySignIn} from "@/features/auth/mutations.ts";
import {getErrorMessage} from "@/lib/errors.ts";
import {notifySuccess} from "@/lib/notify.ts";
import {ErrorAlert} from "@/features/shared/ErrorAlert.tsx";
import {Field} from "@/features/shared/Field.tsx";
import {PasswordRules} from "@/features/auth/components/PasswordRules.tsx";

const changePasswordSchema = z.object({
    login: loginSchema.shape.login,
    currentPassword: passwordSignInSchema.shape.password,
    newPassword: passwordSignUpSchema.shape.password,
    repeatPassword: z.string(),
}).refine(data => data.newPassword === data.repeatPassword.normalize("NFC"), {
    path: ["repeatPassword"],
    message: "The passwords don't match",
});
type FormInput = z.input<typeof changePasswordSchema>;
type FormOutput = z.output<typeof changePasswordSchema>;

const ChangePasswordPage = () => {
    const navigate = useNavigate();
    const {register, handleSubmit, control, formState: {errors, isSubmitting}} = useForm<FormInput, unknown, FormOutput>({
        defaultValues: {login: "", currentPassword: "", newPassword: "", repeatPassword: ""},
        resolver: zodResolver(changePasswordSchema),
    });
    const watchedPassword = useWatch({control, name: "newPassword"});
    const newPassword = typeof watchedPassword === "string" ? watchedPassword : "";
    const {mutate: changePassword, isPending, error} = useChangePasswordBySignIn();

    const onSubmit = (data: FormOutput) => {
        changePassword({
            login: data.login,
            current_password: data.currentPassword,
            new_password: data.newPassword,
        }, {
            onSuccess: () => {
                notifySuccess("Password changed", "Sign in with the new password. Other sessions were signed out.");
                void navigate("/login", {replace: true});
            },
        });
    };

    return (
        <Center minH="100vh" bg="bg.subtle" p="4">
            <Stack boxShadow="md" maxW="md" w="full" p={{base: "6", md: "12"}} rounded="lg" bg="bg.panel">
                <VStack gap="4" align="stretch">
                    <VStack align="stretch" textAlign="left" gap="1">
                        <HStack color="brand.fg"><FiArchive size={26}/><Heading>Change password</Heading></HStack>
                        <Text color="fg.muted">
                            Enter your current password and a new one. Forgot your password? Ask an administrator to
                            set a new one.
                        </Text>
                    </VStack>

                    <VStack align="stretch" as="form" onSubmit={handleSubmit(onSubmit)} gap="3">
                        <Field invalid={!!errors.login} label="Username or email address" errorText={errors.login?.message}>
                            <Input placeholder="Username or email" autoComplete="username"
                                   {...register("login")}/>
                        </Field>

                        <Field invalid={!!errors.currentPassword} label="Current password" errorText={errors.currentPassword?.message}>
                            <Input type="password" autoComplete="current-password"
                                   {...register("currentPassword")}/>
                        </Field>

                        <Field invalid={!!errors.newPassword} label="New password"
                               helperText={<PasswordRules password={newPassword}/>} errorText={errors.newPassword?.message}>
                            <Input type="password" autoComplete="new-password"
                                   {...register("newPassword")}/>
                        </Field>

                        <Field invalid={!!errors.repeatPassword} label="Repeat new password" errorText={errors.repeatPassword?.message}>
                            <Input type="password" autoComplete="new-password"
                                   {...register("repeatPassword")}/>
                        </Field>

                        {!!error && (
                            <ErrorAlert size="sm" description={getErrorMessage(error, "Could not change the password")}/>
                        )}

                        <Button type="submit" loading={isSubmitting || isPending} disabled={isSubmitting || isPending}
                                colorPalette="brand" mt="2">Change password</Button>
                    </VStack>

                    <ChakraLink asChild fontSize="sm" alignSelf="center">
                        <RouterLink to="/login">Back to sign in</RouterLink>
                    </ChakraLink>
                </VStack>
            </Stack>
        </Center>
    );
};

export default ChangePasswordPage;
