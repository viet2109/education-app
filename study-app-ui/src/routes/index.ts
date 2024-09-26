import routers from "../configs/routers.ts";
import Home from "../pages/home.tsx";
import {Route} from "../types";
import DefaultLayout from "../layouts/default.tsx";
import Login from "../pages/login.tsx";
import SignUp from "../pages/signup.tsx";
import Exam from "../pages/exam.tsx";
import PageNotFound from "../pages/pagenotfound.tsx";
import Subject from "../pages/subject.tsx";
import IntroductionExam from "../pages/introductionExam.tsx";

const publicRoutes: Route[] = [
    {path: routers.home, page: Home, layout: DefaultLayout},
    {path: routers.login, page: Login, layout: DefaultLayout},
    {path: routers.signUp, page: SignUp, layout: DefaultLayout},
    {path: routers.subject, page: Subject, layout: DefaultLayout},
    {path: routers.exam, page: Exam, layout: DefaultLayout},
    {path: routers.instructionExam, page: IntroductionExam, layout: DefaultLayout},
    {path: routers.pageNotFound, page: PageNotFound, layout: DefaultLayout}
]

const privateRoutes: Route[] = [
]

export {publicRoutes, privateRoutes};