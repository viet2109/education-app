import {FC} from "react";

export interface Route {
    path: string,
    page: FC<any>,
    layout: FC<any>
}