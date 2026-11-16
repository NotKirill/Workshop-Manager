import {Box, Button, Container, Flex, Stack, Text} from "@chakra-ui/react";
import {Navigate, NavLink, Outlet, useLocation, useMatch} from "react-router";
import {FiActivity, FiMonitor, FiUsers} from "react-icons/fi";
import {useMe} from "@/features/auth/queries.ts";
import type {ReactNode} from "react";
import type {Permission} from "@/features/users/types.ts";

const sections: { path: string; label: string; icon: ReactNode; permission: Permission }[] = [
    {path: "/admin/users", label: "Users", icon: <FiUsers/>, permission: "USERS_MANAGE"},
    {path: "/admin/games", label: "Games", icon: <FiMonitor/>, permission: "GAMES_MANAGE"},
    {path: "/admin/maintenance", label: "Maintenance", icon: <FiActivity/>, permission: "MAINTENANCE_MANAGE"},
];

const SectionLink = ({path, label, icon}: { path: string; label: string; icon: ReactNode }) => {
    const isActive = useMatch({path, end: false}) !== null;
    return (
        <Button asChild w="full" justifyContent="flex-start" variant={isActive ? "subtle" : "ghost"}
                colorPalette={isActive ? "brand" : "gray"}>
            <NavLink to={path}>{icon} {label}</NavLink>
        </Button>
    );
};

export const AdminLayout = () => {
    const {data: me} = useMe();
    const location = useLocation();
    if (!me) return null;
    const allowed = sections.filter(section => me.permissions.some(info => info.permission === section.permission));
    if (allowed.length === 0) {
        return <Navigate to="/library" replace/>;
    }
    if (location.pathname === "/admin" || location.pathname === "/admin/") {
        return <Navigate to={allowed[0].path} replace/>;
    }
    return (
        <Container maxW="8xl" py="6">
            <Flex gap="6" align="start" direction={{base: "column", md: "row"}}>
                <Box w={{base: "full", md: "220px"}} flexShrink={0}>
                    <Text fontWeight="bold" mb="3">Administration</Text>
                    <Stack gap="1">
                        {allowed.map(section => (
                            <SectionLink key={section.path} path={section.path} label={section.label} icon={section.icon}/>
                        ))}
                    </Stack>
                </Box>
                <Box flex="1" minW="0" w="full"><Outlet/></Box>
            </Flex>
        </Container>
    );
};

export default AdminLayout;
