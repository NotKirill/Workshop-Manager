import {
    Badge,
    Button,
    Checkbox,
    Heading,
    HStack,
    IconButton,
    Input,
    Stack,
    Switch,
    Table,
    Text,
} from "@chakra-ui/react";
import {useState} from "react";
import {FiEdit2, FiPlus, FiTrash2} from "react-icons/fi";
import {useAdminUsers, usePermissionInfo} from "@/features/users/queries.ts";
import {
    useCreateUser,
    useDeleteUser,
    useResetUserPassword,
    useSaveUserIdentity,
    useUpdateUser
} from "@/features/users/mutations.ts";
import {ConfirmDialog} from "@/features/shared/ConfirmDialog.tsx";
import {useMe} from "@/features/auth/queries.ts";
import {formatShortDate} from "@/lib/format.ts";
import {notifySuccess} from "@/lib/notify.ts";
import type {AdminUser, AuthSource, Permission} from "@/features/users/types.ts";
import {AppDialog} from "@/features/shared/AppDialog.tsx";
import {Field} from "@/features/shared/Field.tsx";

const DEFAULT_PERMISSIONS: Permission[] = ["ITEMS_READ", "ITEMS_WRITE", "ITEMS_REGISTER", "CONTENT_IMPORT", "MEDIA_UPLOAD"];

const providerLabels: Record<AuthSource, string> = {LOCAL_USERNAME: "user", LOCAL_EMAIL: "email", STEAM: "steam"};

const PermissionChecklist = ({value, onChange}: { value: Permission[]; onChange: (value: Permission[]) => void }) => {
    const {data: permissions} = usePermissionInfo();
    return (
        <Stack gap="2">
            {permissions?.map(info => (
                <Checkbox.Root key={info.permission} checked={value.includes(info.permission)}
                               onCheckedChange={event => onChange(event.checked === true
                                   ? [...value, info.permission] : value.filter(p => p !== info.permission))}>
                    <Checkbox.HiddenInput/>
                    <Checkbox.Control/>
                    <Checkbox.Label>
                        <Text fontWeight="medium">{info.name}</Text>
                        <Text fontSize="xs" color="fg.muted">{info.description}</Text>
                    </Checkbox.Label>
                </Checkbox.Root>
            ))}
        </Stack>
    );
};

const CreateUserDialog = ({open, onOpenChange}: { open: boolean; onOpenChange: (open: boolean) => void }) => {
    const [form, setForm] = useState({name: "", username: "", email: "", password: "", steam_id: ""});
    const [permissions, setPermissions] = useState<Permission[]>(DEFAULT_PERMISSIONS);
    const create = useCreateUser();
    const submit = () => {
        const identities: Partial<Record<AuthSource, string>> = {};
        if (form.username) identities.LOCAL_USERNAME = form.username;
        if (form.email) identities.LOCAL_EMAIL = form.email;
        if (form.steam_id) identities.STEAM = form.steam_id;
        create.mutate({
            name: form.name || null,
            password: form.password || null,
            identities,
            permissions,
        }, {
            onSuccess: () => {
                notifySuccess("User created");
                onOpenChange(false);
            },
        });
    };
    const field = (key: keyof typeof form, label: string, helper?: string, type = "text") => (
        <Field label={label} helperText={helper}>
            <Input type={type} value={form[key]} onChange={event => setForm({...form, [key]: event.target.value})}/>
        </Field>
    );
    return (
        <AppDialog open={open} onOpenChange={onOpenChange} title="New user" lazy closeButton footer={<>
            <Button variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button colorPalette="brand" loading={create.isPending} onClick={submit}>Create</Button>
        </>}>
            <Stack gap="3">
                {field("name", "Display name")}
                {field("username", "Username", "Sign-in with username + password")}
                {field("email", "Email", "Optional alternative to the username")}
                {field("password", "Password", "Required for username/email sign-in", "password")}
                {field("steam_id", "SteamID64", "17 digits; allows signing in with Steam")}
                <Text fontWeight="semibold" mt="2">Permissions</Text>
                <PermissionChecklist value={permissions} onChange={setPermissions}/>
            </Stack>
        </AppDialog>
    );
};

const AdminIdentityRow = ({user, provider}: { user: AdminUser; provider: AuthSource }) => {
    const stored = user.identities.find(identity => identity.provider === provider)?.key ?? "";
    const [value, setValue] = useState(stored);
    const save = useSaveUserIdentity(user.id);
    return (
        <HStack>
            <Badge w="16" justifyContent="center">{providerLabels[provider]}</Badge>
            <Input size="sm" value={value} placeholder={provider === "STEAM" ? "SteamID64" : ""}
                   onChange={event => setValue(event.target.value)}/>
            <Button size="sm" variant="outline" disabled={!value.trim() || value === stored}
                    loading={save.isPending && save.variables.key !== ""}
                    onClick={() => save.mutate({provider, key: value}, {onSuccess: () => notifySuccess("Sign-in method saved")})}>
                Save
            </Button>
            {stored && (
                <Button size="sm" variant="ghost" colorPalette="red" loading={save.isPending && save.variables.key === ""}
                        onClick={() => save.mutate({provider, key: ""}, {onSuccess: () => notifySuccess("Sign-in method removed")})}>
                    Remove
                </Button>
            )}
        </HStack>
    );
};

const EditUserDialog = ({user, onClose}: { user: AdminUser; onClose: () => void }) => {
    const [name, setName] = useState(user.name);
    const [enabled, setEnabled] = useState(user.enabled);
    const [unlocked, setUnlocked] = useState(user.account_non_locked);
    const [permissions, setPermissions] = useState<Permission[]>(user.permissions);
    const [password, setPassword] = useState("");
    const save = useUpdateUser(user.id);
    const resetPassword = useResetUserPassword(user.id);

    return (
        <AppDialog open onOpenChange={open => !open && onClose()} title={`Edit ${user.name}`} size="lg" footer={<>
            <Button variant="outline" onClick={onClose}>Close</Button>
            <Button colorPalette="brand" loading={save.isPending} onClick={() => save.mutate({name, enabled, account_non_locked: unlocked, permissions}, {
                onSuccess: () => {
                    notifySuccess("User updated");
                    onClose();
                },
            })}>Save</Button>
        </>}>
            <Stack gap="4">
                <Field label="Display name">
                    <Input value={name} onChange={event => setName(event.target.value)}/>
                </Field>
                <HStack gap="6">
                    <Switch.Root checked={enabled} onCheckedChange={event => setEnabled(event.checked)}>
                        <Switch.HiddenInput/><Switch.Control/><Switch.Label>Enabled</Switch.Label>
                    </Switch.Root>
                    <Switch.Root checked={!unlocked} onCheckedChange={event => setUnlocked(!event.checked)}>
                        <Switch.HiddenInput/><Switch.Control/><Switch.Label>Locked</Switch.Label>
                    </Switch.Root>
                </HStack>
                <Text fontWeight="semibold">Permissions</Text>
                <PermissionChecklist value={permissions} onChange={setPermissions}/>
                <Stack gap="0">
                    <Text fontWeight="semibold">Sign-in methods</Text>
                    <Text textStyle="hint">
                        Sign-in methods and the password are saved right away with their own buttons; "Save" below
                        stores the name, status and permissions.
                    </Text>
                </Stack>
                {(["LOCAL_USERNAME", "LOCAL_EMAIL", "STEAM"] as AuthSource[]).map(provider => (
                    <AdminIdentityRow key={`${provider}-${user.identities.find(i => i.provider === provider)?.key ?? ""}`}
                                      user={user} provider={provider}/>
                ))}
                <Text fontWeight="semibold">Set password</Text>
                <HStack>
                    <Input size="sm" type="password" value={password}
                           onChange={event => setPassword(event.target.value)}/>
                    <Button size="sm" variant="outline" disabled={password.length < 8}
                            loading={resetPassword.isPending}
                            onClick={() => resetPassword.mutate(password, {
                                onSuccess: () => {
                                    setPassword("");
                                    notifySuccess("Password set");
                                },
                            })}>Set</Button>
                </HStack>
            </Stack>
        </AppDialog>
    );
};

export const AdminUsersPage = () => {
    const {data: me} = useMe();
    const {data: users} = useAdminUsers();
    const {data: permissionInfo} = usePermissionInfo();
    const permissionName = (permission: Permission) =>
        permissionInfo?.find(info => info.permission === permission)?.name ?? permission;
    const [creating, setCreating] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);
    const editing = users?.find(user => user.id === editingId);
    const [deleting, setDeleting] = useState<AdminUser | null>(null);
    const remove = useDeleteUser();

    return (
        <Stack gap="4">
            <HStack justify="space-between">
                <Heading size="lg">Users</Heading>
                <Button colorPalette="brand" size="sm" onClick={() => setCreating(true)}><FiPlus/> New user</Button>
            </HStack>
            <Text textStyle="hint">There is no public sign-up: only accounts created here can sign in.</Text>
            <Table.ScrollArea maxW="full">
                <Table.Root>
                    <Table.Header>
                        <Table.Row>
                            <Table.ColumnHeader>Name</Table.ColumnHeader>
                            <Table.ColumnHeader>Sign-in</Table.ColumnHeader>
                            <Table.ColumnHeader>Permissions</Table.ColumnHeader>
                            <Table.ColumnHeader>Items</Table.ColumnHeader>
                            <Table.ColumnHeader>Created</Table.ColumnHeader>
                            <Table.ColumnHeader/>
                        </Table.Row>
                    </Table.Header>
                    <Table.Body>
                        {users?.map(user => (
                            <Table.Row key={user.id}>
                                <Table.Cell>
                                    <HStack>
                                        <Text>{user.name}</Text>
                                        {!user.enabled && <Badge colorPalette="red">disabled</Badge>}
                                        {!user.account_non_locked && <Badge colorPalette="orange">locked</Badge>}
                                    </HStack>
                                </Table.Cell>
                                <Table.Cell>
                                    <HStack gap="1" wrap="wrap">
                                        {user.identities.map(i => (
                                            <Badge key={i.provider} variant="surface">{providerLabels[i.provider]}: {i.key}</Badge>
                                        ))}
                                    </HStack>
                                </Table.Cell>
                                <Table.Cell>
                                    <HStack gap="1" wrap="wrap">
                                        {user.permissions.map(p => <Badge key={p} colorPalette="purple" size="sm">{permissionName(p)}</Badge>)}
                                    </HStack>
                                </Table.Cell>
                                <Table.Cell>{user.item_count}</Table.Cell>
                                <Table.Cell>{formatShortDate(user.time_created)}</Table.Cell>
                                <Table.Cell textAlign="end">
                                    <IconButton aria-label="Edit" size="xs" variant="ghost" onClick={() => setEditingId(user.id)}>
                                        <FiEdit2/>
                                    </IconButton>
                                    {user.id !== me?.id && (
                                        <IconButton aria-label="Delete" size="xs" variant="ghost" colorPalette="red"
                                                    onClick={() => setDeleting(user)}>
                                            <FiTrash2/>
                                        </IconButton>
                                    )}
                                </Table.Cell>
                            </Table.Row>
                        ))}
                    </Table.Body>
                </Table.Root>
            </Table.ScrollArea>
            <CreateUserDialog open={creating} onOpenChange={setCreating}/>
            {editing && <EditUserDialog user={editing} onClose={() => setEditingId(null)}/>}
            <ConfirmDialog open={deleting !== null} onOpenChange={open => !open && setDeleting(null)} destructive
                           title={`Delete ${deleting?.name ?? ""}?`} confirmLabel="Delete" loading={remove.isPending}
                           onConfirm={() => deleting && remove.mutate(deleting.id, {
                               onSuccess: () => {
                                   notifySuccess("User deleted");
                                   setDeleting(null);
                               },
                           })}>
                The account and its personal library (notes, tags, groups, ratings, media) are deleted. Shared
                metadata and backups are cleaned up automatically if nobody else uses them.
            </ConfirmDialog>
        </Stack>
    );
};

export default AdminUsersPage;
