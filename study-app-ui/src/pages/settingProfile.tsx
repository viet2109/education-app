import { FastField, Form, Formik } from "formik";
import { useSelector } from "react-redux";
import { object, ref, string } from "yup";
import InputField from "../components/inputField";
import { RootState } from "../redux/store";
import { useEffect } from "react";
import Swal from "sweetalert2";

interface Props {}

function SettingProfile(props: Props) {
  const {} = props;
  const ProfileSchema = object({
    name: string().required("Tên là bắt buộc"),
    email: string().required("Email là bắt buộc").email("Email không hợp lệ"),
    phone: string().matches(
      /^(?:\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$/,
      "Số điện thoại không hợp lệ"
    ),
  });
  const PaswordSchema = object({
    password: string().required("Mật khẩu hiện tại là bắt buộc"),
    newpassword: string()
      .required("Mật khẩu mới là bắt buộc")
      .min(6, "Mật khẩu phải có ít nhất 6 ký tự"),
    repassword: string()
      .required("Xác nhận mật khẩu là bắt buộc")
      .oneOf([ref("newpassword")], "Mật khẩu nhập lại phải trùng khớp"),
  });
  const user = useSelector((state: RootState) => state.auth.user);
  return (
    <div className="flex flex-col gap-6">
      <Formik
        initialValues={{
          name: user?.name ? String(user?.name) : "",
          email: user?.email ? String(user?.email) : "",
          phone: user?.phone ? String(user?.phone) : "",
        }}
        validationSchema={ProfileSchema}
        onSubmit={(values) => {
          // Cập nhật thông tin cá nhân
          console.log("Cập nhật thông tin cá nhân", values);
        }}
      >
        {() => (
          <Form className="">
            <h2 className="text-xl font-bold pb-2 border-b-2 mb-5">
              Quản lý thông tin chung
            </h2>
            <ul className="grid grid-cols-1 gap-5 md:grid-cols-2">
              <li className="flex flex-col">
                <label className="mb-2 cursor-pointer " htmlFor="name">
                  Tên:{" "}
                </label>
                <FastField
                  name="name"
                  placeholder="Nhập tên của bạn"
                  component={InputField}
                ></FastField>
              </li>

              <li className="flex flex-col">
                <label className="mb-2 cursor-pointer " htmlFor="email">
                  Email:{" "}
                </label>
                <FastField
                  name="email"
                  placeholder="Nhập email của bạn"
                  component={InputField}
                ></FastField>
              </li>

              <li className="flex flex-col">
                <label className="mb-2 cursor-pointer " htmlFor="phone">
                  Số điện thoại:{" "}
                </label>
                <FastField
                  name="phone"
                  placeholder="Nhập số điện thoại của bạn"
                  component={InputField}
                ></FastField>
              </li>
            </ul>
            <div className="flex justify-center mt-7">
              <button className="btn-custom">Cập nhật thông tin</button>
            </div>
          </Form>
        )}
      </Formik>
      <Formik
        initialValues={{
          password: "",
          newpassword: "",
          repassword: "",
        }}
        validationSchema={PaswordSchema}
        onSubmit={(values) => {
          // Cập nhật mật khẩu
          console.log("Cập nhật mật khẩu", values);
        }}
      >
        {() => (
          <Form className="">
            <h2 className="text-xl font-bold pb-2 border-b-2 mb-5">
              Thay đổi mật khẩu
            </h2>
            <ul className="grid grid-cols-1 gap-5 md:grid-cols-2">
              <li className="flex flex-col">
                <label className="mb-2 cursor-pointer " htmlFor="password">
                  Mật khẩu hiện tại:{" "}
                </label>
                <FastField
                  name="password"
                  placeholder="Nhập mật khẩu hiện tại"
                  component={InputField}
                  type={"password"}
                ></FastField>
              </li>

              <li className="flex flex-col">
                <label className="mb-2 cursor-pointer " htmlFor="newpassword">
                  Mật khẩu mới:{" "}
                </label>
                <FastField
                  name="newpassword"
                  placeholder="Nhập mật khẩu mới"
                  component={InputField}
                  type={"password"}
                ></FastField>
              </li>

              <li className="flex flex-col">
                <label className="mb-2 cursor-pointer " htmlFor="repassword">
                  Xác nhận mật khẩu:{" "}
                </label>
                <FastField
                  name="repassword"
                  placeholder="Xác nhận mật khẩu mới"
                  component={InputField}
                  type={"password"}
                ></FastField>
              </li>
            </ul>
            <div className="flex justify-center mt-7">
              <button className="btn-custom">Cập nhật mật khẩu</button>
            </div>
          </Form>
        )}
      </Formik>
    </div>
  );
}

export default SettingProfile;
