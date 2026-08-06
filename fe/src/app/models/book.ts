import { Genre } from "./genre";

export interface Book {
    id: number,
    title: string,
    author: string,
    genres?: Genre[],
    copies: number,
    borrowed?: boolean,
    coverUrl?: string,
}
