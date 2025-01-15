import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useMemo, useState, type FC } from "react";
import { RiArrowGoBackFill, RiArrowGoForwardFill } from "react-icons/ri";
import { useParams } from "react-router-dom";
import { fetchExamHistoryById } from "../api/examHistory";
import { getAllCategories } from "../api/quiz";
import { images } from "../assets/images";
import QuizMedia from "../components/quizMedia";
import { DEFAULT_SLATE_TIME } from "../constant";
import { Category, ExamHistory } from "../types";
import ImageWithSkeleton from "../components/imageWithSkeleton";

const ExamHistoryDetail: FC = () => {
  const { id } = useParams();
  const [page, setPage] = useState(0);
  const [score, setScore] = useState(0);

  // Fetch exam history data
  const { data: examHistory, isLoading: isExamHistoryLoading } = useQuery<
    ExamHistory,
    Error
  >({
    queryKey: ["exam-histories", id],
    queryFn: () => fetchExamHistoryById(Number(id)),
    staleTime: DEFAULT_SLATE_TIME,
  });

  // Fetch category data
  const { data: categoryList = [], isLoading: isCategoryListLoading } =
    useQuery<Category[], Error>({
      queryKey: ["categories"],
      queryFn: getAllCategories,
      staleTime: DEFAULT_SLATE_TIME,
    });

  const quiz = examHistory?.exam;
  const exams = useMemo(() => quiz?.listQuestion || [], [quiz]);

  const userAnswers = useMemo(() => {
    if (!examHistory?.examHistoryDetail) return [];

    return [
      ...new Map(
        examHistory.examHistoryDetail.map(({ questionId, answerId }) => [
          questionId,
          answerId,
        ])
      ),
    ].map(([questionId, answers]) => ({
      questionId,
      answers: examHistory.examHistoryDetail
        .filter((detail) => detail.questionId === questionId)
        .map((detail) => detail.answerId),
    }));
  }, [examHistory]);

  const correctAnswers = useMemo(() => {
    return exams.map((exam) => ({
      questionId: exam.id,
      answers: exam.listAnswer.filter((a) => a.isCorrect).map((a) => a.id),
    }));
  }, [exams]);

  const checkAnswer = useCallback(
    (questionId: number): number => {
      const currentUserAnswer = userAnswers.find(
        (userAnswer) => userAnswer.questionId === questionId
      );
      const currentCorrectAnswer = correctAnswers.find(
        (correctAnswer) => correctAnswer.questionId === questionId
      );

      if (!currentUserAnswer || !currentCorrectAnswer) return 0;
      if (
        currentCorrectAnswer.answers.length ===
          currentUserAnswer.answers.length &&
        currentCorrectAnswer.answers.every((a) =>
          currentUserAnswer.answers.includes(a || 0)
        )
      ) {
        return 1; // Correct
      }
      if (
        currentCorrectAnswer.answers.every(
          (a) => !currentUserAnswer.answers.includes(a || 0)
        )
      ) {
        return 0; // Incorrect
      }
      return 2; // Partially correct
    },
    [userAnswers, correctAnswers]
  );

  const { totalCorrect, totalInCorrect, totalPartiallyCorrect } =
    useMemo(() => {
      return correctAnswers.reduce(
        (totals, answer) => {
          const result = checkAnswer(answer.questionId || 0);
          if (result === 1) totals.totalCorrect++;
          else if (result === 0) totals.totalInCorrect++;
          else if (result === 2) totals.totalPartiallyCorrect++;
          return totals;
        },
        { totalCorrect: 0, totalInCorrect: 0, totalPartiallyCorrect: 0 }
      );
    }, [checkAnswer, correctAnswers]);

  const goToPage = useCallback((newPage: number) => {
    setPage(newPage);
  }, []);

  const getIsCorrectForAnswer = useCallback(
    (answerId: number): boolean => {
      return correctAnswers.some((correctAnswer) =>
        correctAnswer.answers.includes(answerId)
      );
    },
    [correctAnswers]
  );

  useEffect(() => {
    if (examHistory) setScore(examHistory.score || 0);
  }, [examHistory]);

  return (
    <div className="py-10 lg:flex gap-10">
      <div className="flex flex-col gap-10 flex-1">
        {/* Quiz Info */}
        <div>
          <div className="flex items-center gap-6">
            <p className="text-left">Bài thi:</p>
            <div className="shadow-custom rounded-xl max-w-80 px-6 py-3 flex items-center justify-center">
              {isExamHistoryLoading ? (
                <span className="w-10 inline-block h-4 bg-slate-300 rounded-md animate-pulse"></span>
              ) : (
                <span className="line-clamp-2">{quiz?.title}</span>
              )}
            </div>
          </div>

          <div className="flex items-center gap-6 mt-6">
            <p className="text-left">Môn học:</p>
            <div className="shadow-custom rounded-xl w-fit px-6 py-3 flex items-center justify-center gap-x-3">
              {isCategoryListLoading ? (
                <span className="w-10 inline-block h-4 bg-slate-300 rounded-md animate-pulse"></span>
              ) : (
                <>
                  <ImageWithSkeleton
                    className="w-auto"
                    imgClass="w-16"
                    skeletonClass="!h-12 !w-16"
                    src={
                      categoryList.find(
                        (ct) =>
                          ct.title.trim().replace(/\s+/g, "_").toUpperCase() ===
                          quiz?.category
                      )?.imageUrl || ""
                    }
                    alt="category_image"
                  />
                  <span className="capitalize">
                    {quiz?.category.toLowerCase().replace("_", " ")}
                  </span>
                </>
              )}
            </div>
          </div>
        </div>
        <div className="flex lg:hidden gap-10 justify-center items-center">
          <div className="flex flex-col items-center w-fit gap-2">
            <ImageWithSkeleton
              src={images.score_rank}
              skeletonClass="!w-8 !h-8"
              imgClass="w-8 aspect-square"
              alt="score"
            />
            <p className="flex items-center">
              Score:
              {isExamHistoryLoading ? (
                <span className="w-10 ml-2 inline-block h-4 bg-slate-300 rounded-md animate-pulse"></span>
              ) : (
                <>
                  <sup className="ml-1.5">{score}</sup> / <sub>10</sub>
                </>
              )}
            </p>
          </div>
          <div className="flex flex-col items-center w-fit gap-2">
            <ImageWithSkeleton
              src={images.accuracy}
              skeletonClass="!w-8 !h-8"
              imgClass="w-8 aspect-square"
              alt="Accuracy"
            />

            <p className="flex items-center">
              Accuracy:
              {isExamHistoryLoading ? (
                <span className="w-10 ml-2 inline-block h-4 bg-slate-300 rounded-md animate-pulse"></span>
              ) : (
                <> {Math.round((score / 10) * 100)}% </>
              )}
            </p>
          </div>
        </div>
        {/* Questions */}
        {isExamHistoryLoading ? (
          <div
            className={`block animate-pulse p-4 rounded-lg bg-slate-300 my-4 max-w-xl`}
          >
            <div className="h-8 w-3/4 bg-slate-400 rounded mb-4"></div>
            <div className="h-6 w-full bg-slate-400 rounded mb-4"></div>
            <div className="h-6 w-5/6 bg-slate-400 rounded mb-4"></div>
            <div className="h-6 w-5/6  bg-slate-400 rounded mb-4"></div>
            <div className="h-6 w-2/3 bg-slate-400 rounded"></div>
          </div>
        ) : exams.length > 0 ? (
          <div>
            {exams.map((question, index) => (
              <div
                key={index}
                className={`${index === page ? "block" : "hidden"}`}
              >
                <div className="flex flex-col gap-6">
                  <p className="text-left">Câu hỏi {index + 1}</p>
                  <p className="text-left">{question.content}</p>
                  {question.files.length > 0 && (
                    <QuizMedia files={question.files} />
                  )}
                  <ol className="flex pb-1 flex-col gap-10 group list-[upper-alpha]">
                    {question.listAnswer.map((answer, ansIndex) => (
                      <div key={ansIndex}>
                        <label
                          htmlFor={`${index}-${ansIndex}`}
                          className="flex gap-x-3 items-center"
                        >
                          <input
                            type="checkbox"
                            name={`${question.id}-${answer.id}`}
                            className="peer !hidden"
                            disabled
                            value={answer.id}
                            id={`${index}-${ansIndex}`}
                          />
                          <li
                            className={`flex-1 rounded-lg shadow-custom p-4 list-inside text-left pointer-events-none ${
                              answer.id &&
                              examHistory?.examHistoryDetail
                                .flatMap((detail) => detail.answerId)
                                .includes(answer.id)
                                ? "bg-slate-300 text-white"
                                : ""
                            }`}
                          >
                            {answer.content}
                          </li>
                          {answer?.id && getIsCorrectForAnswer(answer.id) ? (
                            <ImageWithSkeleton
                              src={images.correct_box}
                              skeletonClass="!w-7 !h-7 !rounded-lg"
                              alt="correct-check_box"
                              className="w-auto"
                              imgClass="w-7 aspect-square block"
                            />
                          ) : (
                            <ImageWithSkeleton
                              src={images.incorrect_box}
                              alt="incorrect-check_box"
                              skeletonClass="!w-7 !h-7 !rounded-lg"
                              className="w-auto"
                              imgClass={`w-7 aspect-square ${
                                answer.id &&
                                examHistory?.examHistoryDetail
                                  .flatMap((detail) => detail.answerId)
                                  .includes(answer.id)
                                  ? ""
                                  : "invisible"
                              }`}
                            />
                          )}
                        </label>

                        {answer.files.length > 0 && (
                          <QuizMedia
                            className="mt-3 mb-4"
                            files={answer.files}
                          />
                        )}
                      </div>
                    ))}
                  </ol>
                </div>
              </div>
            ))}

            <div className="mt-6 flex justify-between">
              <button
                type="button"
                onClick={() => goToPage(page - 1)}
                className={`${
                  page > 0 ? "visible" : "invisible"
                } border-none flex items-center justify-center transition-all duration-300 gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white`}
              >
                <RiArrowGoBackFill /> Câu hỏi trước
              </button>
              <div className="flex gap-x-3">
                <button
                  type="button"
                  onClick={() => goToPage(page + 1)}
                  className={`${
                    page < exams.length - 1 ? "visible" : "invisible"
                  } border-none flex items-center transition-all duration-300 justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white`}
                >
                  Câu hỏi tiếp theo <RiArrowGoForwardFill />
                </button>
                <div className="w-7 invisible aspect-square"></div>
              </div>
            </div>
          </div>
        ) : (
          <p>No questions available.</p>
        )}
      </div>

      {/* Sidebar */}
      <div className="hidden lg:block">
        <div className="flex gap-10 justify-center items-center mb-6">
          <div className="flex flex-col items-center w-fit gap-2">
            <ImageWithSkeleton
              src={images.score_rank}
              skeletonClass="!w-8 !h-8"
              imgClass="w-8 aspect-square"
              alt="score"
            />
            <p className="flex items-center">
              Score:
              {isExamHistoryLoading ? (
                <span className="w-10 ml-2 inline-block h-4 bg-slate-300 rounded-md animate-pulse"></span>
              ) : (
                <>
                  <sup className="ml-1.5">{score}</sup> / <sub>10</sub>
                </>
              )}
            </p>
          </div>
          <div className="flex flex-col items-center w-fit gap-2">
            <ImageWithSkeleton
              src={images.accuracy}
              skeletonClass="!w-8 !h-8"
              imgClass="w-8 aspect-square"
              alt="Accuracy"
            />

            <p className="flex items-center">
              Accuracy:
              {isExamHistoryLoading ? (
                <span className="w-10 ml-2 inline-block h-4 bg-slate-300 rounded-md animate-pulse"></span>
              ) : (
                <> {Math.round((score / 10) * 100)}% </>
              )}
            </p>
          </div>
        </div>

        <div className="mt-3 flex flex-col gap-8">
          <div className="flex">
            <ul className="grid grid-cols-5 w-fit gap-6">
              {isExamHistoryLoading
                ? Array.from({ length: 12 }).map((_, index) => (
                    <li
                      key={index}
                      className="shadow-custom w-10 h-10 bg-slate-300 rounded-xl animate-pulse"
                    ></li>
                  ))
                : exams.map((exam, index) => (
                    <li
                      key={index}
                      onClick={() => goToPage(index)}
                      className={`shadow-custom text-white w-10 grid place-items-center rounded-xl cursor-pointer aspect-square ${
                        exam.id &&
                        (checkAnswer(exam.id) === 1
                          ? "bg-primary"
                          : checkAnswer(exam.id) === 0
                          ? "bg-red-400"
                          : "bg-yellow-400")
                      } hover:bg-slate-300 transition-all duration-300`}
                    >
                      {index + 1}
                    </li>
                  ))}
            </ul>
          </div>
          <div className="flex !flex-row gap-8">
            <div className="flex gap-3 items-center">
              <div className="flex flex-col gap-1">
                <p>Correct</p>
                <div className="flex gap-3 items-center">
                  <div className="w-7 aspect-square bg-primary rounded-lg"></div>
                  {isExamHistoryLoading ? (
                    <p className="w-5 h-4 bg-slate-300 rounded-md animate-pulse"></p>
                  ) : (
                    totalCorrect
                  )}
                </div>
              </div>
            </div>
            <div className="flex gap-3 items-center">
              <div className="flex flex-col gap-1">
                <p>Incorrect</p>
                <div className="flex gap-3 items-center">
                  <div className="w-7 aspect-square bg-red-400 rounded-lg"></div>
                  {isExamHistoryLoading ? (
                    <p className="w-5 h-4 bg-slate-300 rounded-md animate-pulse"></p>
                  ) : (
                    totalInCorrect
                  )}
                </div>
              </div>
            </div>
            <div className="flex gap-3 items-center">
              <div className="flex flex-col gap-1">
                <p>Partial</p>
                <div className="flex gap-3 items-center">
                  <div className="w-7 aspect-square bg-yellow-400 rounded-lg"></div>
                  {isExamHistoryLoading ? (
                    <p className="w-5 h-4 bg-slate-300 rounded-md animate-pulse"></p>
                  ) : (
                    totalPartiallyCorrect
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExamHistoryDetail;
