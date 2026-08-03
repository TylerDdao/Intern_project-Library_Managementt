import { User } from "../models/user";

export function getUser(): User | null {
  if (typeof sessionStorage === 'undefined') {
    return null;
  }

  const user = sessionStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}

export function saveUser(user:User):void{
    sessionStorage.setItem("user", JSON.stringify(user));
}