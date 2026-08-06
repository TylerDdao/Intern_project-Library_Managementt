export interface Page {
    number: number,
    totalPages: number,
    last: boolean,
    first: boolean,
    totalElements?:number,
    numberOfElements?: number
}