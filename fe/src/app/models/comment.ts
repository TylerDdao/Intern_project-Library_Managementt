export interface Comment{
    id: number,
    content: string,
    createdBy: string,
    createdAt: string,
    updatedBy?: string,
    updatedAt?: string,
    editable: boolean
}