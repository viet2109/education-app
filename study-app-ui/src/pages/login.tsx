import {FastField, Form, Formik} from "formik";
import type {FC} from "react";
import {object, string} from "yup";
import InputField from "../components/inputField";
import {Link, useNavigate} from "react-router-dom";
import routers from "../configs/routers.ts";
import {LoginSocialFacebook, LoginSocialGoogle} from "reactjs-social-login";
import {FcGoogle} from "react-icons/fc";
import {FaArrowLeft, FaFacebook} from "react-icons/fa";
import {LoginRequest} from "../types";
import {login} from "../api/auth.ts";


const LoginSchema = object({
    email: string().required("Email is required"),
    password: string()
        .required("Password is required")
    ,
});

const Login: FC = () => {
    const navigate = useNavigate();

    async function handleSubmit(loginInfo: LoginRequest) {
        try {
            await login(loginInfo);
            navigate(routers.home);
        } catch (error) {
            console.error(error)
        }
    }

    return (
        <Formik
            initialValues={{
                email: "",
                password: "",
            }}
            validationSchema={LoginSchema}
            onSubmit={(values) => {
                const loginInfo: LoginRequest = {
                    email: values.email,
                    password: values.password,
                };
                // Call your login API with loginInfo
                handleSubmit(loginInfo);
            }}
        >
            {() => (
                <div className={"pt-8 pb-16 max-w-[720px] mx-auto"}>
                    <div
                        className={"flex items-center gap-x-2 mb-6 cursor-pointer w-fit  hover:text-primary"}>
                        <FaArrowLeft/> <Link to={routers.home} className={"font-semibold"}>Home</Link>
                    </div>
                    <Form
                        className="flex flex-col gap-y-4 shadow-custom items-center p-6 rounded-xl">
                        <h1 className={"text-2xl"}>Login</h1>
                        <FastField
                            name="email"
                            placeholder="Enter your email"
                            component={InputField}
                        >
                        </FastField>

                        <FastField
                            name="password"
                            placeholder="Enter your password"
                            component={InputField}
                            type={"password"}
                        >
                        </FastField>

                        <div className={"flex justify-end w-full"}>
                            <Link to={routers.home}
                                  className={"text-[16px] hover:text-primary hover:decoration-primary underline decoration-transparent "}>Forgot
                                password?</Link>
                        </div>

                        <button type={"submit"} className={"w-fit btn-custom"}>Login</button>

                        <div>
                            Don't have account? <Link to={routers.signUp}
                                                      className={"text-[16px] hover:text-primary hover:decoration-primary underline decoration-transparent "}>Sign
                            up</Link>
                        </div>
                        <div className={"flex items-center w-full"}>
                            <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
                            <span className={"px-4"}>Or Login with</span>
                            <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
                        </div>

                        <LoginSocialGoogle className={"w-full"} client_id={import.meta.env.VITE_GG_APP_ID} onReject={""}
                                           onResolve={""}>
                            <button type={"button"}
                                    className={"flex items-center gap-x-3 w-full justify-center border-primary border py-3 rounded-xl hover:bg-primary hover:bg-opacity-25 "}>
                                <FcGoogle size={24}></FcGoogle>
                                Login with Google
                            </button>
                        </LoginSocialGoogle>

                        <LoginSocialFacebook className={"w-full"} client_id={import.meta.env.VITE_FB_APP_ID}
                                             onReject={""} onResolve={""}
                                             appId={""}>
                            <button type={"button"}
                                    className={"flex items-center gap-x-3 w-full justify-center border-primary border py-3 rounded-xl hover:bg-primary hover:bg-opacity-25 "}>
                                <FaFacebook size={24} color={"blue"}></FaFacebook>
                                Login with Google
                            </button>
                        </LoginSocialFacebook>
                    </Form>

                </div>
            )}
        </Formik>
    );
};

export default Login;
