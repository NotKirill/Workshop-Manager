import {useQuery} from "@tanstack/react-query";
import {profileApi} from "@/features/users/api.ts";
import type {Permission} from "@/features/users/types.ts";

export const meQueryKey = ["me"] as const;

export const useMe = () =>
    useQuery({
        queryKey: meQueryKey,
        queryFn: profileApi.get,
        staleTime: 60_000,
    });

export const usePermissions = () => {
    const {data} = useMe();
    return (permission: Permission): boolean => data?.permissions.some(info => info.permission === permission) ?? false;
};
