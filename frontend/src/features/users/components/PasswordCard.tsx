import {Button, Card, Heading, HStack, Input} from "@chakra-ui/react";
import {z} from "zod";
import {useForm, useWatch} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {passwordSignInSchema, passwordSignUpSchema} from "@/features/auth/schemas.ts";
import {useChangePassword} from "@/features/users/mutations.ts";
import {PasswordRules} from "@/features/auth/components/PasswordRules.tsx";
import {Field} from "@/features/shared/Field.tsx";
import {notifySuccess} from "@/lib/notify.ts";

// Without a password yet there is no current one to confirm
const passwordFormSchema = (hasPassword: boolean) => z.object({
    currentPassword: hasPassword ? passwordSignInSchema.shape.password : z.string(),
    newPassword: passwordSignUpSchema.shape.password,
    repeatPassword: z.string(),
}).refine(data => data.newPassword === data.repeatPassword.normalize("NFC"), {
    path: ["repeatPassword"],
    message: "The passwords don't match",
});
type FormInput = z.input<ReturnType<typeof passwordFormSchema>>;
type FormOutput = z.output<ReturnType<typeof passwordFormSchema>>;

export const PasswordCard = ({hasPassword}: { hasPassword: boolean }) => {
    const {register, handleSubmit, reset, control, formState: {errors}} = useForm<FormInput, unknown, FormOutput>({
        defaultValues: {currentPassword: "", newPassword: "", repeatPassword: ""},
        resolver: zodResolver(passwordFormSchema(hasPassword)),
    });
    const watchedPassword = useWatch({control, name: "newPassword"});
    const newPassword = typeof watchedPassword === "string" ? watchedPassword : "";
    const changePassword = useChangePassword();

    const onSubmit = (data: FormOutput) => changePassword.mutate({
        currentPassword: hasPassword ? data.currentPassword : null,
        newPassword: data.newPassword,
    }, {
        onSuccess: () => {
            reset();
            notifySuccess("Password changed");
        },
    });

    return (
        <Card.Root asChild>
            <form onSubmit={handleSubmit(onSubmit)}>
                <Card.Body gap="4">
                    <Heading size="md">{hasPassword ? "Change password" : "Set a password"}</Heading>
                    {hasPassword && (
                        <Field label="Current password" invalid={!!errors.currentPassword}
                               errorText={errors.currentPassword?.message}>
                            <Input type="password" autoComplete="current-password" {...register("currentPassword")}/>
                        </Field>
                    )}
                    <Field label="New password" invalid={!!errors.newPassword}
                           helperText={<PasswordRules password={newPassword}/>} errorText={errors.newPassword?.message}>
                        <Input type="password" autoComplete="new-password" {...register("newPassword")}/>
                    </Field>
                    <Field label="Repeat new password" invalid={!!errors.repeatPassword}
                           errorText={errors.repeatPassword?.message}>
                        <Input type="password" autoComplete="new-password" {...register("repeatPassword")}/>
                    </Field>
                    <HStack justify="flex-end">
                        <Button type="submit" colorPalette="brand" loading={changePassword.isPending}>
                            Save password
                        </Button>
                    </HStack>
                </Card.Body>
            </form>
        </Card.Root>
    );
};

export default PasswordCard;
