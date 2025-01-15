import { FastField, Form, Formik } from "formik";
import type { FC } from "react";
import { FaArrowLeft, FaFacebook } from "react-icons/fa";
import { FcGoogle } from "react-icons/fc";
import { Link } from "react-router-dom";
import { LoginSocialFacebook, LoginSocialGoogle } from "reactjs-social-login";
import { object, ref, string } from "yup";
import { register } from "../api/auth.ts";
import InputField from "../components/inputField";
import routers from "../configs/routers.ts";
import { SignUpRequest } from "../types";

interface SignUpProps {}

const SignUpSchema = object({
  name: string().required("Tên là bắt buộc"),
  email: string().required("Email là bắt buộc").email("Email không hợp lệ"),
  phone: string().matches(
    /^(?:\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$/,
    "Số điện thoại không hợp lệ"
  ),
  password: string()
    .required("Mật khẩu là bắt buộc")
    .min(6, "Mật khẩu phải có ít nhất 6 ký tự"),
  repassword: string()
    .required("Nhập lại mật khẩu là bắt buộc")
    .oneOf([ref("password")], "Mật khẩu nhập lại phải trùng khớp"),
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
        phone: "",
      }}
      validationSchema={SignUpSchema}
      onSubmit={(values) => {
        const sigUpRequest: SignUpRequest = {
          name: values.name,
          email: values.email,
          password: values.password,
          phone: values.phone,
        };
        handleSubmit(sigUpRequest);
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
            <h1 className={"text-2xl"}>Đăng ký</h1>

            <FastField
              name="name"
              placeholder="Nhập tên của bạn"
              component={InputField}
            />

            <FastField
              name="email"
              placeholder="Nhập email của bạn"
              component={InputField}
              type="email"
            />
            <FastField
              name="phone"
              placeholder="Nhập số điện thoại của bạn"
              component={InputField}
            />

            <FastField
              name="password"
              placeholder="Nhập mật khẩu của bạn"
              component={InputField}
              type={"password"}
            />

            <FastField
              name="repassword"
              placeholder="Nhập lại mật khẩu"
              component={InputField}
              type={"password"}
            />

            <button type={"submit"} className={"w-fit btn-custom mt-2"}>
              Đăng ký
            </button>
            <div>
              Đã có tài khoản?{" "}
              <Link
                to={routers.login}
                className={
                  "text-[16px] hover:text-primary hover:decoration-primary underline decoration-transparent "
                }
              >
                Đăng nhập
              </Link>
            </div>
            <div className={"flex items-center w-full"}>
              <span className={"border-t-2 border-t-slate-300 flex-1"}></span>
              <span className={"px-4"}>Hoặc đăng ký với</span>
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
                Đăng ký với Google
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
                Đăng ký với Facebook
              </button>
            </LoginSocialFacebook>
          </Form>
        </div>
      )}
    </Formik>
  );
};
