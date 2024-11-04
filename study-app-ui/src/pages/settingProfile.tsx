import { useSelector } from "react-redux";
import { object, string } from "yup";
import { RootState } from "../redux/store";
import { FastField, Form, Formik } from "formik";
import InputField from "../components/inputField";

interface Props {}

function SettingProfile(props: Props) {
  const {} = props;
  const ProfileSchema = object({
    name: string().required("Name is required"),
    email: string().required("Email is required").email("The email is invalid"),
    phone: string().matches(
      /^(?:\+84|0)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-9]|9[0-9])[0-9]{7}$/,
      "The phone is invalid"
    ),
  });
  const user = useSelector((state: RootState) => state.auth.user);
  return (
    <Formik
      initialValues={{
        name: user?.name ? String(user?.name) : "",
        email: user?.email ? String(user?.email) : "",
        phone: user?.phone ? String(user?.phone) : "",
      }}
      validationSchema={ProfileSchema}
      onSubmit={(values) => {
        // Update profile
        console.log("Update profile", values);
      }}
    >
      {() => (
        <Form className="">
          <h2 className="text-xl font-bold pb-2 border-b-2 mb-5">Manage your profile</h2>
          <ul className="grid grid-cols-1 gap-5 md:grid-cols-2">
            <li className="flex flex-col">
              <label className="mb-2 cursor-pointer " htmlFor="name">Name: </label>
              <FastField
                name="name"
                placeholder="Enter your name"
                component={InputField}
              ></FastField>
            </li>

            <li className="flex flex-col">
              <label className="mb-2 cursor-pointer " htmlFor="email">Email: </label>
              <FastField
                name="email"
                placeholder="Enter your email"
                component={InputField}
              ></FastField>
            </li>

            <li className="flex flex-col">
              <label className="mb-2 cursor-pointer " htmlFor="phone">Phone: </label>
              <FastField
                name="phone"
                placeholder="Enter your phone"
                component={InputField}
              ></FastField>
            </li>
          </ul>
          <div className="flex justify-center mt-7">
            <button className="btn-custom">Update profile</button>
          </div>
        </Form>
      )}
    </Formik>
  );
}

export default SettingProfile;
