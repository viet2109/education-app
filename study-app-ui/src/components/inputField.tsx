import {FC, useState} from "react";
import {FaEye, FaEyeSlash} from "react-icons/fa";

interface InputFieldProps {
    field: any;
    form: any;
    type?: string;
    placeholder?: string;
    id?: string;
    className?: string;
}

const InputField: FC<InputFieldProps> = (props) => {
    const {type = "text", placeholder, field, form, id, className} = props;
    const {name, value, onChange, onBlur} = field;
    const {errors, touched} = form;
    const [isHidePass, setIsHidePass] = useState(true);

    function handleShowHidePass() {
        setIsHidePass((prev) => !prev);
    }

    // Đặt lại type dựa vào trạng thái hiển thị/ẩn mật khẩu
    let inputType = type === "password" && isHidePass ? "password" : "text";
    if (type !== "password") inputType = type;

    return (
        <div className={"relative w-full"}>
            <div className={"relative"}>
                <input
                    type={inputType}
                    id={id || name}
                    name={name}
                    value={value}
                    placeholder={placeholder}
                    onChange={onChange}
                    onBlur={onBlur}
                    className={`outline-none w-full border-2 px-4 py-3 rounded-lg focus:border-primary ${errors[name] && touched[name] && "!border-red-300"}  caret-primary ${className}`}
                />
                {type === "password" && (
                    isHidePass ? (
                        <FaEyeSlash
                            onClick={handleShowHidePass}
                            size={36}
                            className={"absolute p-2 hover:bg-slate-100 rounded-full  right-5 top-1/2 -translate-y-1/2 cursor-pointer"}
                        />
                    ) : (
                        <FaEye
                            onClick={handleShowHidePass}
                            size={36}
                            className={"absolute p-2 hover:bg-slate-100 rounded-full  right-5 top-1/2 -translate-y-1/2 cursor-pointer"}
                        />
                    )
                )}
            </div>

            {errors[name] && touched[name] && (
                <div className="text-red-300 text-xs mt-1.5">{errors[name]}</div>
            )}
        </div>
    );
};

export default InputField;
