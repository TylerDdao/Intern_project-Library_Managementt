import { Feature } from "./feature";

export interface Role {
    id: number,
    name: string,
    features: [Feature]
}