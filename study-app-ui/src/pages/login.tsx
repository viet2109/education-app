import { FastField, Form, Formik } from "formik";
import { useState, type FC } from "react";
import { FaArrowLeft, FaFacebook } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";
import { Link, useNavigate } from "react-router-dom";
import { LoginSocialFacebook, LoginSocialGoogle } from "reactjs-social-login";
import { object, string } from "yup";
import { login } from "../api/auth.ts";
import InputField from "../components/inputField";
import routers from "../configs/routers.ts";
import { LoginRequest } from "../types";

const LoginSchema = object({
  email: string().required("Email là bắt buộc"),
  password: string().required("Mật khẩu là bắt buộc"),
});

const Login: FC = () => {
  const navigate = useNavigate();
  const [error, setError] = useState(false);

  async function handleSubmit(loginInfo: LoginRequest) {
    try {
      await login(loginInfo);
      navigate(routers.home);
    } catch (error) {
      console.error(error);
      setError(true);
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
        // Gọi API đăng nhập với loginInfo
        handleSubmit(loginInfo);
      }}
    >
      {() => (
        <div className={"pt-8 pb-16 max-w-[720px] mx-auto"}>
          <div
            className={
              "flex items-center gap-x-2 mb-6 cursor-pointer w-fit  hover:text-primary"
            }
          >
            <FaArrowLeft />{" "}
            <Link to={routers.home} className={"font-semibold"}>
              Trang chủ
            </Link>
          </div>
          <Form className="flex flex-col gap-y-4 shadow-custom items-center p-6 rounded-xl">
            <h1 className={"text-2xl"}>Đăng nhập</h1>
            <FastField
              name="email"
              placeholder="Nhập email của bạn"
              component={InputField}
            ></FastField>

            <FastField
              name="password"
              placeholder="Nhập mật khẩu của bạn"
              component={InputField}
              type={"password"}
            ></FastField>

            <div className={"flex justify-end w-full"}>
              <Link
                to={routers.forgotPass}
                className={
                  "text-[16px] hover:text-primary hover:decoration-primary underline decoration-transparent "
                }
              >
                Quên mật khẩu?
              </Link>
            </div>
            {error && (
              <span className="text-red-500 text-sm text-center">
                Email hoặc mật khẩu không đúng, vui lòng kiểm tra lại.
              </span>
            )}
            <button type={"submit"} className={"w-fit btn-custom"}>
              Đăng nhập
            </button>

            <div>
              Chưa có tài khoản?{" "}
              <Link
                to={routers.signUp}
                className={
                  "text-[16px] hover:text-primary hover:decoration-primary underline decoration-transparent "
                }
              >
                Đăng ký
              </Link>
            </div>
            <div className={"flex items-center w-full"}>
              <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
              <span className={"px-4"}>Hoặc đăng nhập với</span>
              <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
            </div>

            <LoginSocialGoogle
              className={"w-full"}
              client_id={import.meta.env.VITE_GG_APP_ID}
              onReject={""}
              onResolve={""}
            >
              <button
                type={"button"}
                className={
                  "flex items-center gap-x-3 w-full justify-center border-primary border py-3 rounded-xl hover:bg-primary hover:bg-opacity-25 "
                }
              >
                <FcGoogle size={24}></FcGoogle>
                Đăng nhập với Google
              </button>
            </LoginSocialGoogle>

            <LoginSocialFacebook
              className={"w-full"}
              client_id={import.meta.env.VITE_FB_APP_ID}
              onReject={""}
              onResolve={""}
              appId={""}
            >
              <button
                type={"button"}
                className={
                  "flex items-center gap-x-3 w-full justify-center border-primary border py-3 rounded-xl hover:bg-primary hover:bg-opacity-25 "
                }
              >
                <FaFacebook size={24} color={"blue"}></FaFacebook>
                Đăng nhập với Facebook
              </button>
            </LoginSocialFacebook>
          </Form>
        </div>
      )}
    </Formik>
  );
};

export default Login;
