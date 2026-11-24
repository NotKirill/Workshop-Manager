import {Button, Field, HStack, Input, Link as ChakraLink, VStack} from "@chakra-ui/react";
import {Link as RouterLink} from "react-router";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {loginSchema, passwordSignInSchema} from "@/features/auth/schemas.ts";
import {useLogin} from "@/features/auth/mutations.ts";
import type {AuthRequest} from "@/features/auth/types.ts";
import {getErrorMessage} from "@/lib/errors.ts";
import {ErrorAlert} from "@/features/shared/ErrorAlert.tsx";

const signInFormSchema = z.object({
    login: loginSchema.shape.login,
    password: passwordSignInSchema.shape.password,
});
type FormInput = z.input<typeof signInFormSchema>;
type FormOutput = z.output<typeof signInFormSchema>;

export const AuthenticationSection = () => {
    const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm<FormInput, unknown, FormOutput>({
        defaultValues: {
            login: "",
            password: "",
        },
        resolver: zodResolver(signInFormSchema),
    });

    const {mutate: login, isPending, error} = useLogin();

    const onSubmit = (data: FormOutput) => {
        const request: AuthRequest = {
            login: data.login,
            password: data.password,
        };

        login(request);
    };

    return (
        <VStack minW="full" align="stretch" as="form" onSubmit={handleSubmit(onSubmit)} p="0" gap="3">
            <Field.Root invalid={!!errors.login}>
                <Field.Label htmlFor="login">Username or email address</Field.Label>
                <Input id="login" placeholder="Username or email" autoComplete="username" {...register("login")}/>
                <Field.ErrorText overflowWrap="anywhere">{errors.login?.message}</Field.ErrorText>
            </Field.Root>

            <Field.Root invalid={!!errors.password}>
                <HStack justify="space-between" w="full">
                    <Field.Label htmlFor="password">
                        Password
                    </Field.Label>
                    <ChakraLink asChild fontSize="sm">
                        <RouterLink to="/change-password">Change password</RouterLink>
                    </ChakraLink>
                </HStack>
                <Input id="password" type="password" placeholder="Password" autoComplete="current-password"
                       {...register("password")}/>
                <Field.ErrorText overflowWrap="anywhere">{errors.password?.message}</Field.ErrorText>
            </Field.Root>

            {!!error && (
                <ErrorAlert size="sm" description={getErrorMessage(error, "Sign-in failed")}/>
            )}

            <Button type="submit" loading={isSubmitting || isPending} disabled={isSubmitting || isPending}
                    colorPalette='brand' mt="4">Sign in</Button>
        </VStack>
    );
};