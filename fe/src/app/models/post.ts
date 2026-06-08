import { Book } from "./book";

export interface Post {
    id: number,
    subject: string,
    content: string,
    likeCount: string,
    book: Book
    createdBy: string,
    createdAt: string,
    updatedBy?: string,
    updatedAt?: string,
    
}