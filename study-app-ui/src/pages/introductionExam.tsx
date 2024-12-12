import { useCallback, useEffect, useState, type FC } from "react";
import { FaRegClock, FaRegQuestionCircle } from "react-icons/fa";
import { NavLink, useParams } from "react-router-dom";
import { images } from "../assets/images";
import routers from "../configs/routers.ts";
import { fetchQuiz } from "../api/quiz.ts";
import { Quiz } from "../types/index.ts";
import { useQuery } from "@tanstack/react-query";

interface IntroductionExamProps {}

const IntroductionExam: FC<IntroductionExamProps> = () => {
  const { id } = useParams();

  const [quiz, setQuiz] = useState<Quiz>();
  const { data, isLoading, refetch } = useQuery<Quiz>({
    queryKey: ["quizz", id],
    queryFn: () => fetchQuiz(Number(id)),
  });

  useEffect(() => {
    setQuiz(data);
  }, [data]);

  useEffect(() => {
    refetch();
  }, [id]);
  return (
    <div className="py-10 grid place-items-center">
      {isLoading ? (
        <div className="mb-10 w-96 max-w-96 h-8 bg-gray-300 rounded-md animate-pulse"></div>
      ) : (
        <h1 className="mb-10 font-[500] capitalize text-xl max-w-96 text-center">
          {quiz?.title}
        </h1>
      )}

      <div className="flex flex-col lg:flex-row">
        <div>
          <div className="flex justify-between mb-10">
            <div className="flex justify-center items-center gap-4 text-primary">
              <FaRegQuestionCircle size={24} className="text-gray-400" />
              {isLoading ? (
                <span className="inline-block w-32 h-6 bg-gray-300 rounded-md animate-pulse"></span>
              ) : (
                <span>QUESTIONS: {quiz?.listQuestion.length}</span>
              )}
            </div>
            <div className="flex justify-center items-center gap-4 text-primary">
              <FaRegClock size={24} className="text-gray-400" />

              {isLoading ? (
                <span className="inline-block w-32 h-6 bg-gray-300 rounded-md animate-pulse"></span>
              ) : (
                <span>TIME: {quiz?.duration && quiz?.duration / 60} Mins</span>
              )}
            </div>
          </div>
          <div>
            <h2 className="font-[500] text-xl text-left">Before you Start</h2>
            <ul className="pl-10 flex flex-col gap-8 mt-8">
              <li className="list-disc marker:text-primary text-left">
                You are about to practice official questions set of subject{" "}
                {quiz?.category.toLocaleLowerCase()}.
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

      {isLoading ? (
        <div className="block w-fit mt-10 py-4 px-10 bg-gray-300 rounded-lg animate-pulse">
          <div className="h-6 w-20 bg-gray-400 rounded"></div>
        </div>
      ) : (
        <NavLink
          to={routers.exam}
          state={quiz}
          className="btn-custom block w-fit mt-10 !py-4 !px-10"
        >
          Start Exam
        </NavLink>
      )}
    </div>
  );
};

export default IntroductionExam;
