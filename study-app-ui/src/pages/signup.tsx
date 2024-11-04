import {FastField, Form, Formik} from "formik";
import type {FC} from "react";
import {object, ref, string} from "yup";
import InputField from "../components/inputField";
import {Link} from "react-router-dom";
import routers from "../configs/routers.ts";
import {LoginSocialFacebook, LoginSocialGoogle} from "reactjs-social-login";
import {FcGoogle} from "react-icons/fc";
import {FaArrowLeft, FaFacebook} from "react-icons/fa";
import {SignUpRequest} from "../types";
import {register} from "../api/auth.ts";

interface SignUpProps {
}

const SignUpSchema = object({
    name: string().required("Name is required"),
    email: string().required("Email is required").email("The email is invalid"),
    phone: string().matches(/^(?:\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$/, "The phone is invalid"),
    password: string()
        .required("Password is required")
        .min(6, "The password must be at least 6 characters"),
    repassword: string().required("Repassword is required").oneOf([ref("password")], "Repassword must be matches with password")
});

export const SignUp: FC<SignUpProps> = () => {
    function handleSubmit(signUpRequest: SignUpRequest) {
        register(signUpRequest);
    }

    return (
        <Formik
            initialValues={{
                name: "",
                email: "",
                password: "",
                phone: ""
            }}
            validationSchema={SignUpSchema}
            onSubmit={(values) => {
                const sigUpRequest: SignUpRequest = {
                    name: values.name,
                    email: values.email,
                    password: values.password,
                    phone: values.phone
                };
                // Call your login API with loginInfo
                handleSubmit(sigUpRequest);
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
                        <h1 className={"text-2xl"}>Sign Up</h1>

                        <FastField
                            name="name"
                            placeholder="Enter your name"
                            component={InputField}
                        >
                        </FastField>

                        <FastField
                            name="email"
                            placeholder="Enter your email"
                            component={InputField}
                            type="email"
                        >
                        </FastField>
                        <FastField
                            name="phone"
                            placeholder="Enter your phone"
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

                        <FastField
                            name="repassword"
                            placeholder="Enter repassword"
                            component={InputField}
                            type={"password"}
                        >
                        </FastField>

                        <button type={"submit"} className={"w-fit btn-custom mt-2"}>Sign up</button>
                        <div>
                            Already have account? <Link to={routers.login}
                                                        className={"text-[16px] hover:text-primary hover:decoration-primary underline decoration-transparent "}>Login</Link>
                        </div>
                        <div className={"flex items-center w-full"}>
                            <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
                            <span className={"px-4"}>Or Sign up with</span>
                            <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
                        </div>

                        <LoginSocialGoogle className={"w-full"} client_id={import.meta.env.VITE_GG_APP_ID} onReject={""} onResolve={""}>
                            <button type={"button"}
                                    className={"flex items-center gap-x-3 w-full justify-center border-primary border py-3 rounded-xl hover:bg-primary hover:bg-opacity-25 "}>
                                <FcGoogle size={24}></FcGoogle>
                                Sign up with Google
                            </button>
                        </LoginSocialGoogle>

                        <LoginSocialFacebook className={"w-full"} client_id={import.meta.env.VITE_FB_APP_ID} onReject={""} onResolve={""}
                                             appId={""}>
                            <button type={"button"}
                                    className={"flex items-center gap-x-3 w-full justify-center border-primary border py-3 rounded-xl hover:bg-primary hover:bg-opacity-25 "}>
                                <FaFacebook size={24} color={"blue"}></FaFacebook>
                                Sign up with Facebook
                            </button>
                        </LoginSocialFacebook>
                    </Form>

                </div>
            )}
        </Formik>
    );
};
