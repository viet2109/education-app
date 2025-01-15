import { FormEvent, useRef } from "react";
import { images } from "../assets/images";
import { useMutation } from "@tanstack/react-query";
import { forgotPass } from "../api/auth";
import Swal from "sweetalert2";
import { FaPaperPlane, FaPlane } from "react-icons/fa6";
import ImageWithSkeleton from "../components/imageWithSkeleton";

function ForgotPassword() {
  const input = useRef<HTMLInputElement>(null);
  const mutation = useMutation({
    mutationFn: (email: string) => {
      console.log(email);
      return forgotPass(input.current?.value || "ss");
    },
    onSuccess: () => {
      Swal.fire({
        text: "Email đã được gửi thành công!",
        icon: "success",
      });
    },
    onError: (error: any) => {
      Swal.fire({
        text: `Có lỗi xảy ra: ${
          error.response.data.errorMessage || "Không xác định"
        }`,
        icon: "error",
      });
    },
  });
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    mutation.mutate(input.current?.value || "");
  };
  return (
    <div className="py-10">
      <div className="flex py-10 max-w-xl m-auto flex-col gap-y-3 items-center shadow-custom">
        <ImageWithSkeleton
          imgClass="w-40"
          skeletonClass="!w-40 !h-20"
          src={images.logo}
          alt="logo"
        />
        <h1 className="text-3xl">Quên mật khẩu</h1>
        <p>Hãy nhập email của bạn để tiếp tục nhé.</p>

        <form
          className="flex  gap-3"
          onSubmit={(e) => {
            handleSubmit(e);
          }}
        >
          <input
            ref={input}
            title="Email không hợp lệ"
            required
            pattern="^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$"
            placeholder="Nhập địa chỉ email"
            className="outline outline-1 outline-gray-300 transition-all focus:outline-2 focus:outline-primary rounded-lg border-none p-4"
            type="email"
          />
          <button type="submit" className="btn-custom flex gap-2 items-center">
            <span>Gửi</span>
            <FaPaperPlane />
          </button>
        </form>
      </div>
    </div>
  );
}

export default ForgotPassword;
