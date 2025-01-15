const routers ={
    home: "/",
    login: "/login",
    signUp: "/signup",
    instructionExam: "/exams/:id",
    exams: "/exams",
    exam: "/exam",
    subject: "/subjects",
    forgotPass: "/forgot-password",
    settingProfile: "/settings/profile",
    examHistoryDetail: "/exam-histories/:id",
    examHistories: "/exam-histories",
    settingQuiz: "/settings/quizzes",
    settingQuizDetails: "/settings/quizzes/:id",
    pageNotFound: "*",
}

export default routers;