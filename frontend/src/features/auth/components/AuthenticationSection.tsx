import {Button, Field, HStack, Input, Link as ChakraLink, VStack} from "@chakra-ui/react";
import {Link as RouterLink} from "react-router"
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {emailSchema, passwordSignInSchema, usernameSchema} from "@/features/auth/schemas.ts";
import {useLogin} from "@/features/auth/mutations.ts";
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
    password: passwordSignInSchema.shape.password,
});
type FormInput = z.input<typeof loginSchema>;
type FormOutput = z.output<typeof loginSchema>;

export const AuthenticationSection = () => {
    const {register, handleSubmit, formState: {errors, isSubmitting}} = useForm<FormInput, unknown, FormOutput>({
        defaultValues: {
            login: "",
            password: "",
        },
        resolver: zodResolver(loginSchema),
    });

    const {mutate: login, isPending} = useLogin();

    // TODO server errors??? setError in useForm???????
    const onSubmit = (data: FormOutput) => {
        const request: AuthRequest = {
            login: data.login,
            password: data.password,
        };

        login(request);
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