import {
  FaFacebook,
  FaInstagram,
  FaPhone,
  FaTwitter,
  FaYoutube,
} from "react-icons/fa";
import { FaLocationDot } from "react-icons/fa6";
import { IoMdMail } from "react-icons/io";
import { images } from "../assets/images";
import ImageWithSkeleton from "./imageWithSkeleton";

function Footer() {
  return (
    <footer className={"bg-[#2d3e52] px-default text-white py-10"}>
      <div className={"max-w-default mx-auto flex flex-col gap-y-6 "}>
        <div className={"flex flex-col gap-y-6 lg:flex-row lg:gap-x-14"}>
          {/*introduce*/}
          <div className={"flex flex-col gap-y-4 lg:basis-4/12"}>
            <strong>Tại sao chọn Exambly.com?</strong>
            <p>
              Exambly cung cấp luyện thi miễn phí cho mọi người để vượt qua mọi
              kỳ thi thành công.
            </p>
            <p>
              Luyện thi với Exambly thật thú vị vì bạn sẽ nhận được điểm thưởng
              cho câu trả lời đúng, duy trì động lực với phần thưởng, học nhanh
              hơn và nhận kết quả ngay lập tức.
            </p>
          </div>

          {/*contact*/}
          <div
            className={
              " border-t-2 pt-8 lg:border-none lg:pt-0 flex flex-col gap-y-8 md:flex-row md:gap-x-8 md:justify-between lg:basis-8/12 lg:gap-x-14"
            }
          >
            <div className={"flex flex-col gap-y-4"}>
              <strong className="line-clamp-1">Về chúng tôi</strong>
              <p>Blog</p>
              <p className="line-clamp-1">Truyền thông</p>
              <p className="line-clamp-1">Nghề nghiệp</p>
              <p className="line-clamp-1">Đội ngũ</p>
              <p className="line-clamp-1">Cộng đồng</p>
              <p>Đối tác</p>
            </div>

            <div className={"flex flex-col gap-y-4"}>
              <strong>Liên hệ</strong>
              <p className={"flex items-center gap-x-3"}>
                <IoMdMail className={"min-w-6"} size={24} />
                <span>hello@Exambly.com</span>
              </p>

              <p className={"flex items-center gap-x-3"}>
                <FaPhone className={"min-w-5"} size={20} />
                <span>+ 234 802 785 5262</span>
              </p>
              <p className={"flex items-center gap-x-3"}>
                <FaLocationDot className={"min-w-[22px]"} size={22} />
                <span>
                  6 Gbemisola Street, Allen Avenue, Ikeja, Lagos, Nigeria.
                </span>
              </p>
            </div>

            <div className={"flex flex-col gap-y-4"}>
              <strong className="line-clamp-1">Thống kê chung</strong>
              <p className={"flex flex-col gap-y-1"}>
                <span className="line-clamp-1">Bài kiểm tra hoàn thành</span>
                <span className={"text-2xl"}>480,050</span>
              </p>

              <p className={"flex flex-col gap-y-1"}>
                <span>Khách hàng</span>
                <span className={"text-2xl"}>39,160</span>
              </p>

              <p className={"flex flex-col gap-y-1"}>
                <span className="line-clamp-1">Tỷ lệ vượt trung bình</span>
                <span className={"text-2xl"}>87%</span>
              </p>
            </div>
          </div>
        </div>

        {/*copy-right*/}
        <div
          className={
            "flex border-t-2 pt-8 flex-col gap-y-4 md:flex-row md:justify-between"
          }
        >
          <ImageWithSkeleton
            className="!block"
            src={images.logo2}
            imgClass="w-32"
            alt={"Logo"}
          />
          <div className={"flex flex-col gap-y-4 md:flex-row md:gap-x-4"}>
            <p className="line-clamp-1">Chính sách bảo mật</p>
            <p className="line-clamp-1">Bản quyền</p>
            <p className="line-clamp-1">Điều khoản dịch vụ</p>
          </div>
          <div className={"flex gap-x-4"}>
            <FaFacebook size={28} />
            <FaTwitter size={28} />
            <FaInstagram size={28} />
            <FaYoutube size={28} />
          </div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;
