import { Book } from "./book";
import { User } from "./user";

export interface Borrow{
    id: number,
    book: Book
    user: User,
    dueDate: string,
    createdAt: string,
    
    updatedAt: string,
    // updatedBy: string,
    // createdBy: string,
}