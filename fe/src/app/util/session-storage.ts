import { User } from "../models/user";

export function getUser(): User | null {
    const user = sessionStorage.getItem("user");

    if (!user) {
        return null;
    }

    return JSON.parse(user) as User;
}