import { useQuery } from "@tanstack/react-query";
import { useMemo } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ExamHistoryFilter, fetchExamHistories } from "../api/examHistory";
import routers from "../configs/routers";
import { formatLocalDateTime } from "../helper/formatLocalDate";
import { ExamHistory, Page } from "../types";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";
import Pagianate from "../components/paginate";
interface Answer {
  questionId: number;
  answers: number[];
}
function ExamHistories() {
  const queryParams = useMemo(
    () => new URLSearchParams(location.search),
    [location.search]
  );
  const user = useSelector((state: RootState) => state.auth.user);

  const paginationFilter: ExamHistoryFilter = useMemo(
    () => ({
      page: queryParams.has("page")
        ? Number(queryParams.get("page")) - 1
        : undefined,
      size: queryParams.has("size")
        ? Number(queryParams.get("size"))
        : undefined,
      sort: queryParams.has("sort")
        ? queryParams.getAll("sort").map((s) => s)
        : undefined,
      userId: user?.id,
    }),
    [queryParams]
  );

  const { data, isLoading } = useQuery<Page<ExamHistory>, Error>({
    queryKey: ["exam-histories", paginationFilter],
    queryFn: () => fetchExamHistories(paginationFilter),
  });

  const getUserAnswers = (examhistoryId: number): Answer[] => {
    const examHistory = data?.content.find((e) => e.id === examhistoryId);

    if (!examHistory) return [];

    const questionMap = new Map<number, number[]>();

    examHistory.examHistoryDetail.forEach(({ questionId, answerId }) => {
      if (!questionMap.has(questionId)) {
        questionMap.set(questionId, []);
      }
      questionMap.get(questionId)!.push(answerId);
    });

    return Array.from(questionMap.entries()).map(([questionId, answers]) => ({
      questionId,
      answers,
    }));
  };

  const getCorrectAnswers = (examhistoryId: number): Answer[] => {
    const examHistory = data?.content.find((e) => e.id === examhistoryId);

    if (!examHistory?.exam?.listQuestion) {
      return [];
    }

    return examHistory.exam.listQuestion
      .filter((question) => question.id !== undefined) // Filter out undefined question IDs
      .map((question) => ({
        questionId: question.id as number, // Ensure questionId is defined
        answers: question.listAnswer
          .filter((answer) => answer.isCorrect && answer.id !== undefined) // Filter undefined answers
          .map((answer) => answer.id as number), // Ensure answer IDs are defined
      }));
  };

  const checkAnswer = (
    questionId: number,
    userAnswers: Answer[],
    correctAnswers: Answer[]
  ) => {
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
  };
  const navigate = useNavigate();
  const getTotalAnswer = (
    examhistoryId: number
  ): {
    totalCorrect: number;
    totalInCorrect: number;
    totalPartiallyCorrect: number;
  } => {
    const correctAnswers = getCorrectAnswers(examhistoryId);
    const userAnswers = getUserAnswers(examhistoryId);

    return correctAnswers.reduce(
      (totals, answer) => {
        const result = checkAnswer(
          answer.questionId,
          userAnswers,
          correctAnswers
        );
        switch (result) {
          case 1:
            totals.totalCorrect++;
            break;
          case 0:
            totals.totalInCorrect++;
            break;
          case 2:
            totals.totalPartiallyCorrect++;
            break;
        }
        return totals;
      },
      { totalCorrect: 0, totalInCorrect: 0, totalPartiallyCorrect: 0 }
    );
  };
  const handlePageChange = (newPage: number) => {
    const params = new URLSearchParams(location.search);
    params.set("page", (newPage + 1).toString());
    navigate({ search: params.toString() });
  };

  return (
    <>
      {isLoading ? (
        <div>
          <div className="flex justify-between px-4 py-2 font-bold text-xl border-2 border-b-transparent rounded-tl-lg rounded-tr-lg border-gray-300">
            <h1 className="line-clamp-1">Progress</h1>
            <div>
              <p className="line-clamp-1">
                Total Tests Taken:{" "}
                <span className="inline-block w-10 h-4 bg-gray-300 rounded animate-pulse"></span>
              </p>
            </div>
          </div>
          <ul className="bg-slate-200 rounded-bl-lg rounded-br-lg *:bg-white *:rounded-lg *:px-5 *:py-3 p-4 flex flex-col gap-4">
            {[...Array(1)].map((_, index) => (
              <li
                key={index}
                className="flex flex-col gap-3 lg:gap-6 lg:flex-row lg:items-start animate-pulse"
              >
                <div className="flex flex-col gap-1">
                  <p className="w-24 h-6 bg-gray-300 rounded"></p>
                  <p className="w-36 h-4 bg-gray-300 rounded"></p>
                  <p className="w-48 h-4 bg-gray-300 rounded"></p>
                  <p className="w-48 h-4 bg-gray-300 rounded"></p>
                </div>
                <div className="flex flex-col *:lg:border-l-2 *:lg:border-gray-300 lg:flex-row flex-1 lg:justify-start items-center *:flex *:flex-col *:items-center gap-2 lg:gap-0 *:px-4 lg:pt-1">
                  <p className="lg:first:border-none flex flex-col gap-1 items-center">
                    <span className="w-20 h-4 bg-gray-300 rounded"></span>
                    <span className="w-10 h-6 bg-gray-300 rounded"></span>
                  </p>
                  <p className="lg:first:border-none flex flex-col gap-1 items-center">
                    <span className="w-20 h-4 bg-gray-300 rounded"></span>
                    <span className="w-10 h-6 bg-gray-300 rounded"></span>
                  </p>
                  <p className="lg:first:border-none flex flex-col gap-1 items-center">
                    <span className="w-20 h-4 bg-gray-300 rounded"></span>
                    <span className="w-10 h-6 bg-gray-300 rounded"></span>
                  </p>
                  <p className="lg:first:border-none flex flex-col gap-1 items-center">
                    <span className="w-20 h-4 bg-gray-300 rounded"></span>
                    <span className="w-10 h-6 bg-gray-300 rounded"></span>
                  </p>
                  <p className="lg:first:border-none flex flex-col gap-1 items-center">
                    <span className="w-20 h-4 bg-gray-300 rounded"></span>
                    <span className="w-10 h-6 bg-gray-300 rounded"></span>
                  </p>
                </div>
                <button className="btn-custom mt-1 w-32 h-10 bg-gray-300 rounded"></button>
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <>
          <div>
            <div className="flex justify-between px-4 py-2 font-bold text-xl border-2 border-b-transparent rounded-tl-lg rounded-tr-lg border-gray-300">
              <h1 className="line-clamp-1">Progress</h1>
              <div>
                <p className="line-clamp-1">
                  Total Tests Taken: {data?.totalElements}
                </p>
              </div>
            </div>
            <ul className="bg-slate-200 rounded-bl-lg rounded-br-lg *:bg-white *:rounded-lg *:px-5 *:py-3 p-4 flex flex-col gap-4">
              {data?.content.map((examHistory) => (
                <li
                  key={examHistory.id}
                  className="flex flex-col gap-3 lg:gap-6 lg:flex-row lg:items-start"
                >
                  <div className="flex flex-col gap-1">
                    <p className="text-lg font-bold line-clamp-1">
                      {examHistory.exam.title}
                    </p>
                    <p>
                      <span className="text-gray-400">
                        {examHistory.exam.listQuestion.length} Questions |{" "}
                      </span>{" "}
                      <span className="text-primary">
                        {Math.round(examHistory.exam.duration / 60)} mins
                      </span>
                    </p>
                    <p>
                      <strong>Started at</strong>:{" "}
                      {formatLocalDateTime(examHistory.startedAt)}
                    </p>
                    <p>
                      <strong>Finished at</strong>:{" "}
                      {formatLocalDateTime(examHistory.finishedAt)}
                    </p>
                  </div>
                  <div className="flex flex-col *:lg:border-l-2 *:lg:border-gray-300  lg:flex-row flex-1 lg:justify-start items-center *:flex *:flex-col *:items-center gap-2 lg:gap-0 *:px-4 lg:pt-1">
                    <p className="lg:first:border-none">
                      <span className="uppercase line-clamp-1 font-semibold text-gray-400">
                        correct
                      </span>
                      <span className="text-xl font-semibold text-primary line-clamp-1">
                        {getTotalAnswer(examHistory.id).totalCorrect}
                      </span>
                    </p>
                    <p className="lg:first:border-none">
                      <span className="uppercase line-clamp-1 font-semibold text-gray-400">
                        wrong
                      </span>
                      <span className="line-clamp-1 text-xl font-semibold text-red-400">
                        {getTotalAnswer(examHistory.id).totalInCorrect}
                      </span>
                    </p>

                    <p className="lg:first:border-none">
                      <span className="uppercase line-clamp-1 font-semibold text-gray-400">
                        Partially Correct
                      </span>
                      <span className="line-clamp-1 text-xl font-semibold text-yellow-400">
                        {getTotalAnswer(examHistory.id).totalPartiallyCorrect}
                      </span>
                    </p>
                    <p className="lg:first:border-none">
                      <span className="uppercase line-clamp-1 font-semibold text-gray-400">
                        Accuary
                      </span>
                      <span className=" text-xl font-semibold text-gray-400 line-clamp-1">
                        {Math.round((examHistory.score / 10) * 100)} %
                      </span>
                    </p>
                    <p className="lg:first:border-none">
                      <span className="uppercase line-clamp-1 font-semibold text-gray-400">
                        remark
                      </span>
                      <span className="uppercase text-xl font-semibold text-gray-400 line-clamp-1">
                        {Math.round((examHistory.score / 10) * 100) > 50
                          ? "pass"
                          : "fail"}
                      </span>
                    </p>
                    <p className="lg:first:border-none">
                      <span className="uppercase line-clamp-1 font-semibold text-gray-400">
                        velocity
                      </span>
                      <span className="uppercase text-xl font-semibold text-gray-400 line-clamp-1">
                        {Math.floor(
                          (new Date(examHistory.finishedAt).getTime() -
                            new Date(examHistory.startedAt).getTime()) /
                            1000
                        ) >
                        examHistory.exam.duration / 2
                          ? "Normal"
                          : "Fast"}
                      </span>
                    </p>
                  </div>
                  <button className="btn-custom mt-1">
                    <Link
                      className="line-clamp-1"
                      to={routers.examHistoryDetail.replace(
                        ":id",
                        examHistory.id.toString()
                      )}
                    >
                      See detail
                    </Link>
                  </button>
                </li>
              ))}
            </ul>
          </div>
          <Pagianate
            onPageChange={handlePageChange}
            itemsLength={data?.totalElements || 0}
            numberItemOnPage={data?.pageable.pageSize || 0}
            initialPage={paginationFilter.page || 0}
          />
        </>
      )}
    </>
  );
}

export default ExamHistories;
