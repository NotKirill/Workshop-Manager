import {z} from "zod";

// TODO add to backend all validations
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


export const passwordSchema = z.object({
    password: z
        .string()
        .min(6, "Password must be at least 6 characters")
        .max(128, "Password must be at most 128 characters"),
});
export type Password = z.infer<typeof passwordSchema>;


export const emailSchema = z.object({
    email: z
        .email("Please enter a valid email address")
        .trim()
        .toLowerCase()
        .min(3, "Email address must be at least 3 characters")
        .max(254, "Email address is too long")
});
export type Email = z.infer<typeof emailSchema>;