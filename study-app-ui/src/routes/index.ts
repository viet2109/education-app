import routers from "../configs/routers.ts";
import Home from "../pages/home.tsx";
import {Route} from "../types";
import DefaultLayout from "../layouts/default.tsx";
import Login from "../pages/login.tsx";
import {SignUp} from "../pages/signup.tsx";
import Exam from "../pages/exam.tsx";
import PageNotFound from "../pages/pagenotfound.tsx";
import Subject from "../pages/subject.tsx";
import IntroductionExam from "../pages/introductionExam.tsx";
import Exams from "../pages/exams.tsx";
import SettingProfile from "../pages/settingProfile.tsx";
import SettingLayout from "../layouts/settingLayout.tsx";
import SettingQuiz from "../pages/settingQuiz.tsx";
import SettingQuizDetails from "../pages/settingQuizDetails.tsx";

const publicRoutes: Route[] = [
    {path: routers.home, page: Home, layout: DefaultLayout},
    {path: routers.login, page: Login, layout: DefaultLayout},
    {path: routers.signUp, page: SignUp, layout: DefaultLayout},
    {path: routers.subject, page: Subject, layout: DefaultLayout},
    {path: routers.exams, page: Exams, layout: DefaultLayout},
    {path: routers.pageNotFound, page: PageNotFound, layout: DefaultLayout},
    {path: routers.instructionExam, page: IntroductionExam, layout: DefaultLayout}
]

const privateRoutes: Route[] = [
    {path: routers.exam, page: Exam, layout: DefaultLayout},
    {path: routers.settingProfile, page: SettingProfile, layout: SettingLayout},
    {path: routers.settingQuiz, page: SettingQuiz, layout: SettingLayout},
    {path: routers.settingQuizDetails, page: SettingQuizDetails, layout: SettingLayout},
]

export {publicRoutes, privateRoutes};