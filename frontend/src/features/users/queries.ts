import {useQuery} from "@tanstack/react-query";
import {adminUsersApi} from "@/features/users/api.ts";

export const adminUsersQueryKey = ["admin", "users"] as const;
export const permissionInfoQueryKey = ["admin", "permissions"] as const;

export const useAdminUsers = () =>
    useQuery({queryKey: adminUsersQueryKey, queryFn: adminUsersApi.list});

export const usePermissionInfo = () =>
    useQuery({queryKey: permissionInfoQueryKey, queryFn: adminUsersApi.permissions});
