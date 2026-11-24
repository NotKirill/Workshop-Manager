import {
    Badge,
    Button,
    Card,
    Container,
    Heading,
    HStack,
    Image,
    Input,
    Stack,
    Text,
} from "@chakra-ui/react";
import {useState} from "react";
import {useSearchParams} from "react-router";
import {useMe} from "@/features/auth/queries.ts";
import {
    useRemoveIdentity,
    useRenameProfile,
    useSetIdentity,
    useStartSteamLink
} from "@/features/users/mutations.ts";
import SteamIcon from "@/assets/steam.svg";
import {formatDate} from "@/lib/format.ts";
import {notifySuccess} from "@/lib/notify.ts";
import type {AuthSource, Profile} from "@/features/users/types.ts";
import {ErrorAlert} from "@/features/shared/ErrorAlert.tsx";
import {redirectErrorMessage} from "@/features/auth/redirectErrors.ts";
import {Field} from "@/features/shared/Field.tsx";
import {PasswordCard} from "@/features/users/components/PasswordCard.tsx";

const IdentityRow = ({profile, provider, label}: { profile: Profile; provider: AuthSource; label: string }) => {
    const current = profile.identities.find(identity => identity.provider === provider);
    const [value, setValue] = useState(current?.key ?? "");
    const save = useSetIdentity();
    const remove = useRemoveIdentity();
    return (
        <Field label={label}>
            <HStack w="full">
                <Input value={value} onChange={event => setValue(event.target.value)}/>
                <Button variant="outline" disabled={!value.trim() || value === current?.key} loading={save.isPending}
                        onClick={() => save.mutate({provider, key: value},
                            {onSuccess: () => notifySuccess(`${label} saved`)})}>Save</Button>
                {current && (
                    <Button variant="ghost" colorPalette="red" loading={remove.isPending}
                            onClick={() => remove.mutate(provider, {onSuccess: () => setValue("")})}>Remove</Button>
                )}
            </HStack>
        </Field>
    );
};

export const ProfilePage = () => {
    const [searchParams] = useSearchParams();
    const {data: profile} = useMe();
    const [name, setName] = useState<string | null>(null);

    const rename = useRenameProfile();
    const linkSteam = useStartSteamLink();
    const removeIdentity = useRemoveIdentity();

    if (!profile) return null;
    const steam = profile.identities.find(identity => identity.provider === "STEAM");
    const storedKey = (provider: AuthSource) =>
        profile.identities.find(identity => identity.provider === provider)?.key ?? "none";
    const error = searchParams.get("error");

    return (
        <Container maxW="3xl" py="6">
            <Stack gap="6">
                <Heading size="xl">Profile</Heading>
                {error && (
                    <ErrorAlert title={redirectErrorMessage(error, "Steam account could not be linked.")}/>
                )}
                <Card.Root>
                    <Card.Body gap="4">
                        <Stack gap="2">
                            <Text color="fg.muted">Member since {formatDate(profile.time_created)} · {profile.item_count} item(s)</Text>
                            {profile.permissions.length > 0 && (
                                <HStack wrap="wrap">{profile.permissions.map(info => (
                                    <Badge key={info.permission} colorPalette="purple" title={info.description ?? undefined}>
                                        {info.name}
                                    </Badge>
                                ))}</HStack>
                            )}
                        </Stack>
                        <Field label="Display name">
                            <HStack w="full">
                                <Input value={name ?? profile.name} onChange={event => setName(event.target.value)}/>
                                <Button variant="outline" disabled={!name || name === profile.name}
                                        loading={rename.isPending} onClick={() => rename.mutate(name ?? "", {
                                            onSuccess: () => {
                                                setName(null);
                                                notifySuccess("Name updated");
                                            },
                                        })}>Save</Button>
                            </HStack>
                        </Field>
                    </Card.Body>
                </Card.Root>

                <Card.Root>
                    <Card.Body gap="4">
                        <Heading size="md">Sign-in methods</Heading>
                        <IdentityRow key={storedKey("LOCAL_USERNAME")} profile={profile} provider="LOCAL_USERNAME" label="Username"/>
                        <IdentityRow key={storedKey("LOCAL_EMAIL")} profile={profile} provider="LOCAL_EMAIL" label="Email"/>
                        <HStack justify="space-between" borderWidth="1px" borderRadius="md" p="3">
                            <HStack>
                                <Image src={SteamIcon} alt="" boxSize="24px"/>
                                <Text>{steam ? `Steam account ${steam.key}` : "No Steam account linked"}</Text>
                            </HStack>
                            {steam ? (
                                <Button size="sm" variant="ghost" colorPalette="red" loading={removeIdentity.isPending}
                                        onClick={() => removeIdentity.mutate("STEAM")}>Unlink</Button>
                            ) : (
                                <Button size="sm" variant="outline" loading={linkSteam.isPending}
                                        onClick={() => linkSteam.mutate()}>Link Steam account</Button>
                            )}
                        </HStack>
                    </Card.Body>
                </Card.Root>

                <PasswordCard key={String(profile.has_password)} hasPassword={profile.has_password}/>
            </Stack>
        </Container>
    );
};

export default ProfilePage;
