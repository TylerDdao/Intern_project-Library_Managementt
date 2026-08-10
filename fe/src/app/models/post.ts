import { Book } from "./book";

export interface Post {
    id?: number,
    subject: string,
    content: string,
    likeCount: number,
    commentCount: number,
    book: Book,
    createdBy: string,
    createdAt: string,
    liked: boolean,
    editable: boolean
}