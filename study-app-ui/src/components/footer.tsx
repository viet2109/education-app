import {images} from "../assets/images";
import {FaFacebook, FaInstagram, FaPhone, FaTwitter, FaYoutube} from "react-icons/fa";
import {IoMdMail} from "react-icons/io";
import {FaLocationDot} from "react-icons/fa6";

function Footer() {
    return (
        <footer
            className={"bg-[#2d3e52] px-default text-white py-10"}>
            <div className={"max-w-default mx-auto flex flex-col gap-y-6 "}>
                <div className={"flex flex-col gap-y-6 lg:flex-row lg:gap-x-14"}>
                    {/*introduce*/}
                    <div className={"flex flex-col gap-y-4 lg:basis-4/12"}>
                        <strong>Why Exambly.com</strong>
                        <p>Exambly offers free exam practice for Africans to ace any examination successfully.</p>
                        <p>Preparing for exams with Exambly is fun as you earn points for correct answers, stay
                            motivated
                            with
                            rewards, learn faster, and receive instant results.</p>
                    </div>

                    {/*contact*/}
                    <div
                        className={" border-t-2 pt-8 lg:border-none lg:pt-0 flex flex-col gap-y-8 md:flex-row md:justify-between lg:basis-8/12 lg:gap-x-14"}>
                        <div className={"flex flex-col gap-y-4"}>
                            <strong>About</strong>
                            <p>Blog</p>
                            <p>Media</p>
                            <p>Careers</p>
                            <p>Our Team</p>
                            <p>Community</p>
                            <p>Partnership</p>
                        </div>

                        <div className={"flex flex-col gap-y-4"}>
                            <strong>Contact</strong>
                            <p className={"flex items-center gap-x-3"}>
                                <IoMdMail className={"min-w-6"} size={24}/>
                                <span>hello@Exambly.com</span>
                            </p>

                            <p className={"flex items-center gap-x-3"}>
                                <FaPhone className={"min-w-5"} size={20}/>
                                <span>+ 234 802 785 5262</span>
                            </p>
                            <p className={"flex items-center gap-x-3"}>
                                <FaLocationDot className={"min-w-[22px]"} size={22}/>
                                <span>6 Gbemisola Street, Allen Avenue, Ikeja, Lagos, Nigeria.</span>
                            </p>

                        </div>

                        <div className={"flex flex-col gap-y-4"}>
                            <strong>Key Stats</strong>
                            <p className={"flex flex-col gap-y-1"}>
                                <span>Tests Completed</span>
                                <span className={'text-2xl'}>480,050</span>
                            </p>

                            <p className={"flex flex-col gap-y-1"}>
                                <span>Customers</span>
                                <span className={'text-2xl'}>39,160</span>
                            </p>

                            <p className={"flex flex-col gap-y-1"}>
                                <span>Avg. Pass Rate</span>
                                <span className={'text-2xl'}>87%</span>
                            </p>


                        </div>
                    </div>

                </div>

                {/*copy-right*/}
                <div className={"flex border-t-2 pt-8 flex-col gap-y-4 md:flex-row md:justify-between"}>
                    <img className={"w-32"} src={images.logo2} alt="Logo"/>
                    <div className={"flex flex-col gap-y-4 md:flex-row md:gap-x-4"}>
                        <p>Privacy Policy</p>
                        <p>Copyright</p>
                        <p>Terms of Service</p>
                    </div>
                    <div className={"flex gap-x-4"}>
                        <FaFacebook size={28}/>
                        <FaTwitter size={28}/>
                        <FaInstagram size={28}/>
                        <FaYoutube size={28}/>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;