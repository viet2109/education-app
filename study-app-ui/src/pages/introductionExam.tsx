import type { FC } from "react";
import { FaRegClock, FaRegQuestionCircle } from "react-icons/fa";
import { NavLink } from "react-router-dom";
import {images} from "../assets/images";
import routers from "../configs/routers.ts";

interface IntroductionExamProps {}

const IntroductionExam: FC<IntroductionExamProps> = () => {
    return (
        <div className="py-10 grid place-items-center">
            <h1 className="mb-10 font-[500] capitalize text-xl">
                WASSCE/GCE Economics 2008
            </h1>
            <div className="flex flex-col lg:flex-row">
                <div>
                    <div className="flex justify-between mb-10">
                        <div className="flex justify-center items-center gap-4 text-primary">
                            <FaRegQuestionCircle size={24} className="text-gray-400" />
                            <span>QUESTIONS: 50</span>
                        </div>
                        <div className="flex justify-center items-center gap-4 text-primary">
                            <FaRegClock size={24} className="text-gray-400" />
                            <span>TIME: 60 Mins</span>
                        </div>
                    </div>
                    <div>
                        <h2 className="font-[500] text-xl text-left">Before you Start</h2>
                        <ul className="pl-10 flex flex-col gap-8 mt-8">
                            <li className="list-disc marker:text-primary text-left">
                                You are about to practice official questions set for WASSCE/GCE.
                            </li>
                            <li className="list-disc marker:text-primary text-left">
                                At the end of your exam practice, you can tap on review to view
                                correct answers and solutions.
                            </li>
                            <li className="list-disc marker:text-primary text-left">
                                Your results won’t be displayed without your permission.
                            </li>
                            <li className="list-disc marker:text-primary text-left">
                                To begin your exam practice, simply tap the START button.
                            </li>
                            <li className="text-primary text-left">
                                You’ve got this, and we wish you the very best!
                            </li>
                        </ul>
                    </div>
                </div>
                <div className="relative grid place-items-center -z-10 lg:basis-1/3">
                    <img
                        src={images.rocket}
                        className="object-contain w-1/2 lg:w-full as -rotate-45"
                        alt="rocket"
                    />
                </div>
            </div>
            <NavLink to={routers.exam} className="btn-custom block w-fit mt-10 !py-4 !px-10">
                Start Exam
            </NavLink>
        </div>
    );
};

export default IntroductionExam;
