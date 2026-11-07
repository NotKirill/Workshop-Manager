import {Avatar, Box, Button, Container, Flex, HStack, Menu, Portal, Spinner, Text} from "@chakra-ui/react";
import {Link as RouterLink, NavLink, Outlet, useMatch, useNavigate} from "react-router";
import {FiArchive, FiFolder, FiGrid, FiLogOut, FiSettings, FiUser} from "react-icons/fi";
import {ColorModeButton} from "@/components/ui/chakra/color-mode.tsx";
import {useMe} from "@/features/auth/queries.ts";
import {useLogout} from "@/features/auth/mutations.ts";
import type {ReactNode} from "react";
import {usePermissions} from "@/features/auth/queries.ts";

const NavItem = ({to, icon, label}: { to: string; icon: ReactNode; label: string }) => {
    const isActive = useMatch({path: to, end: false}) !== null;
    return (
        <Button asChild variant={isActive ? "subtle" : "ghost"} size="sm" colorPalette={isActive ? "brand" : "gray"}>
            <NavLink to={to} aria-label={label}>
                {icon} <Text as="span" hideBelow="sm">{label}</Text>
            </NavLink>
        </Button>
    );
};

export const AppShell = () => {
    const {data: me, isLoading} = useMe();
    const {mutate: logout} = useLogout();
    const navigate = useNavigate();
    const can = usePermissions();
    const isAdmin = can("USERS_MANAGE") || can("GAMES_MANAGE") || can("MAINTENANCE_MANAGE");
    const canRead = can("ITEMS_READ");

    const onLogout = () => {
        logout(undefined, {
            onSettled: () => {
                void navigate("/login", {replace: true});
            },
        });
    };

    return (
        <Flex direction="column" minH="100vh" bg="bg.subtle">
            <Box as="header" h="header" borderBottomWidth="1px" bg="bg.panel" position="sticky" top="0" zIndex="docked">
                <Container maxW="8xl" py="2">
                    <Flex align="center" justify="space-between" gap={{base: "2", sm: "4"}}>
                        <HStack gap={{base: "2", sm: "6"}}>
                            <HStack asChild gap="2">
                                <RouterLink to="/library" aria-label="Workshop Manager">
                                    <Box color="brand.fg"><FiArchive size={22}/></Box>
                                    <Text fontWeight="bold" fontSize="lg" hideBelow="sm">Workshop Manager</Text>
                                </RouterLink>
                            </HStack>
                            <HStack gap="1">
                                {canRead && <NavItem to="/library" icon={<FiGrid/>} label="Library"/>}
                                {canRead && <NavItem to="/organize" icon={<FiFolder/>} label="Organize"/>}
                                {isAdmin && <NavItem to="/admin" icon={<FiSettings/>} label="Admin"/>}
                            </HStack>
                        </HStack>
                        <HStack gap={{base: "1", sm: "2"}}>
                            <ColorModeButton/>
                            {isLoading ? <Spinner size="sm"/> : (
                                <Menu.Root positioning={{placement: "bottom-end"}}>
                                    <Menu.Trigger asChild>
                                        <Button variant="ghost" size="sm" aria-label="Account menu">
                                            <Avatar.Root size="xs">
                                                <Avatar.Fallback name={me?.name ?? "?"}/>
                                            </Avatar.Root>
                                            <Text hideBelow="md">{me?.name}</Text>
                                        </Button>
                                    </Menu.Trigger>
                                    <Portal>
                                        <Menu.Positioner>
                                            <Menu.Content>
                                                <Menu.Item value="profile" onClick={() => void navigate("/profile")}>
                                                    <FiUser/> Profile
                                                </Menu.Item>
                                                <Menu.Item value="logout" onClick={onLogout}>
                                                    <FiLogOut/> Sign out
                                                </Menu.Item>
                                            </Menu.Content>
                                        </Menu.Positioner>
                                    </Portal>
                                </Menu.Root>
                            )}
                        </HStack>
                    </Flex>
                </Container>
            </Box>
            <Box as="main" flex="1">
                <Outlet/>
            </Box>
        </Flex>
    );
};

export default AppShell;
