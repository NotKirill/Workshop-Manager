import {Button, Field, HStack, Input, Link as ChakraLink, VStack} from "@chakra-ui/react";
import {Link as RouterLink} from "react-router"
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {emailSchema, passwordSchema, usernameSchema} from "@/features/auth/schemas.ts";
import {useLoginByEmail, useLoginByUsername} from "@/features/auth/mutations.ts";
import type {AuthRequest} from "@/features/auth/types.ts";

const loginSchema = z.object({
    login: z.string().superRefine((val, ctx) => {
        const result = val.includes("@")
            ? emailSchema.shape.email.safeParse(val)
            : usernameSchema.shape.username.safeParse(val);

        if (!result.success) {
            result.error.issues.forEach((issue) => {
                ctx.addIssue(issue as Parameters<typeof ctx.addIssue>[0]);
            });
        }
    }),
    password: passwordSchema.shape.password,
});

type FormData = z.infer<typeof loginSchema>;

export const AuthenticationSection = () => {
    // TODO remove authMethods constants and forms

    const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm<FormData>({
        defaultValues: {
            login: "",
            password: "",
        },
        resolver: zodResolver(loginSchema),
    });

    const {mutate: loginByEmail, isPending: isEmailPending} = useLoginByEmail();
    const {mutate: loginByUsername, isPending: isUsernamePending} = useLoginByUsername();

    const isPending = isEmailPending || isUsernamePending;

    // TODO server errors??? setError in useForm???????
    const onSubmit = (data: FormData) => {
        const request: AuthRequest = {
            login: data.login,
            password: data.password,
        };

        const mutation = data.login.includes("@")
            ? loginByEmail
            : loginByUsername;

        mutation(request);
    };

    return (
        <VStack minW="full" align="stretch" as="form" onSubmit={handleSubmit(onSubmit)} p={0} gap={3}>
            <Field.Root invalid={!!errors.login}>
                <Field.Label htmlFor="login" fontWeight="bold">Username or email address</Field.Label>
                <Input id="login" placeholder="Username or email" {...register("login")}/>
                <Field.ErrorText overflowWrap="anywhere">{errors.login?.message}</Field.ErrorText>
            </Field.Root>

            <Field.Root invalid={!!errors.password}>
                <HStack justify="space-between" w="full">
                    <Field.Label htmlFor="password" fontWeight="bold">
                        Password
                    </Field.Label>

                    <ChakraLink asChild fontSize="sm">
                        {/*TODO*/}
                        <RouterLink to="/forgot-password">Forgot password?</RouterLink>
                    </ChakraLink>
                </HStack>
                <Input id="password" type="password" placeholder="Password" {...register("password")}/>
                <Field.ErrorText overflowWrap="anywhere">{errors.password?.message}</Field.ErrorText>
            </Field.Root>

            <Button type="submit" loading={isSubmitting || isPending} disabled={isSubmitting || isPending}
                    colorPalette='teal' mt={4}>Sign in</Button>
        </VStack>
    );
};