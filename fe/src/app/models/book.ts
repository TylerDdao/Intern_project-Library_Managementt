export interface Book {
    id: number,
    title: string,
    author: string,
    genres?: string[],
    copies: number,
    borrowed?: boolean,
    coverUrl?: string,
}
