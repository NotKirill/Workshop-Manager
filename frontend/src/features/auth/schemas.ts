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


export const passwordSignUpSchema = z.object({
    password:
        z.preprocess((val) =>
                (typeof val === "string" ? val.normalize("NFC") : val),
            z.string()
                .min(8, "Password must be at least 8 characters")
                .max(128, "Password must be at most 128 characters")
                .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+}{';?>.<,]).*$/, "Password must include uppercase, lowercase, a number, and a special character")
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