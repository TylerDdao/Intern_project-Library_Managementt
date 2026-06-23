export interface User{
    id: number,
    fullName: string,
    email: string,
    phoneNumber: string,
    address?: string,
    role: string,
    username: string,
    password?: string 
    authorities?: string[];
}