import { Role } from "./role";

export interface User{
    id?: number,
    fullName: string,
    email: string,
    phoneNumber: string,
    address?: string,
    role?: Role,
    username: string,
    password?: string 
    authorities?: string[];
}