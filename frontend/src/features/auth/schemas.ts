import {z} from "zod";


export const usernameSchema = z.object({
    username: z
        .string()
        .trim()
        .toLowerCase()
        .min(3, "Username must be at least 3 characters")
        .max(50, "Username must be at most 50 characters")
        .regex(/^[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*$/, "Username may only contain alphanumeric characters or single hyphens, and cannot begin or end with a hyphen"),
});
export type Username = z.infer<typeof usernameSchema>;


export const passwordSignInSchema = z.object({
    password:
        z.preprocess((val) =>
                (typeof val === "string" ? val.normalize("NFC") : val),
            z.string()
                .min(8, "Password must be at least 8 characters")
                .max(128, "Password must be at most 128 characters")
        ),
});
export type PasswordSignIn = z.infer<typeof passwordSignInSchema>;

// Backup: .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+}{';?>.<,]).*$/,
//  "Password must include uppercase, lowercase, a number, and a special character")
const PASSWORD_SPECIAL_CHARACTERS = "!@#$%^&*()_+}{';?>.<,";

export interface PasswordRule {
    label: string;
    test: (password: string) => boolean;
}

export const PASSWORD_RULES: PasswordRule[] = [
    {label: "8–128 characters", test: password => password.length >= 8 && password.length <= 128},
    {label: "a lowercase letter", test: password => /[a-z]/.test(password)},
    {label: "an uppercase letter", test: password => /[A-Z]/.test(password)},
    {label: "a number", test: password => /\d/.test(password)},
    {
        label: `a special character: ${PASSWORD_SPECIAL_CHARACTERS.split("").join(" ")}`,
        test: password => [...password].some(character => PASSWORD_SPECIAL_CHARACTERS.includes(character)),
    },
];

export const passwordSignUpSchema = z.object({
    password:
        z.preprocess((val) =>
                (typeof val === "string" ? val.normalize("NFC") : val),
            z.string().superRefine((value, ctx) => {
                for (const rule of PASSWORD_RULES) {
                    if (!rule.test(value)) {
                        ctx.addIssue({code: "custom", message: `Password needs ${rule.label}`});
                    }
                }
            })
        ),
});
export type PasswordSignUp = z.infer<typeof passwordSignUpSchema>;


export const emailSchema = z.object({
    email: z
        .email("Please enter a valid email address")
        .trim()
        .toLowerCase()
        .min(3, "Email address must be at least 3 characters")
        .max(254, "Email address is too long")
});
export type Email = z.infer<typeof emailSchema>;


export const loginSchema = z.object({
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
});
export type Login = z.infer<typeof loginSchema>;
