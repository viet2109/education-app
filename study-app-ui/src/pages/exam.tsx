import ProgressBar from "@ramonak/react-progress-bar";
import {
  FormEvent,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
  type FC,
} from "react";
import Countdown from "react-countdown";
import { FaLocationArrow } from "react-icons/fa";
import { RiArrowGoBackFill, RiArrowGoForwardFill } from "react-icons/ri";
import { useLocation } from "react-router-dom";
import Swal from "sweetalert2";
import { getAllCategories, submitExam } from "../api/quiz";
import { Category, QuestionResponseDto, Quiz, QuizAnswer } from "../types";
import { useQuery } from "@tanstack/react-query";
import { DEFAULT_SLATE_TIME } from "../constant";
import QuizMedia from "../components/quizMedia";
import { images } from "../assets/images";

interface ExamProps {}
type InputObject = Record<string, FormDataEntryValue>;

const Exam: FC<ExamProps> = () => {
  const quiz: Quiz = useLocation().state;
  const [page, setPage] = useState(0);
  const [totalAns, setTotalAns] = useState<Array<number>>([]);
  const form = useRef<HTMLFormElement>(null);
  const [score, setScore] = useState(0);
  const [quizAnswer, setQuizAnswer] = useState<QuestionResponseDto[]>();
  const [userAnswer, setUserAnswer] = useState<QuizAnswer[]>();

  const targetDate = useMemo(() => Date.now() + 1000 * 3600 * 3, []);
  const [isSubmit, setIsSubmit] = useState(false);

  const {
    data: categoryList = [],
    isLoading,
    error,
  } = useQuery<Category[], Error>({
    queryKey: ["categories"],
    queryFn: getAllCategories,
    staleTime: DEFAULT_SLATE_TIME,
  });

  const [examList] = useState(() => {
    return quiz.listQuestion;
  });

  const exams = useMemo(() => examList, []);

  const handleAnswerChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>, examsIndex: number) => {
      if (e.currentTarget.checked) {
        setTotalAns((prev) => [...prev, examsIndex]);
      } else {
        setTotalAns((prev) => {
          const index = prev.indexOf(examsIndex);
          if (index !== -1) {
            return [...prev.slice(0, index), ...prev.slice(index + 1)];
          }
          return [...prev];
        });
      }
    },
    [totalAns]
  );

  const goToPage = useCallback((newPage: number) => {
    setPage(newPage);
  }, []);

  function handleSubmit(e: FormEvent<HTMLFormElement>): void {
    e.preventDefault();
    const text =
      new Set(totalAns).size === exams.length
        ? "Are you sure you want to submit the exam?"
        : `You have ${
            exams.length - new Set(totalAns).size
          } unanswered questions. Are you sure you want to submit the exam?`;
    Swal.fire({
      title: "Confirm submit",
      text,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Yes, submit!",
      background: "#fff",
    }).then((result) => {
      if (result.isConfirmed) {
        submitForm();
      }
    });
  }

  const transformObjectToArray = (obj: InputObject): QuizAnswer[] => {
    const result: Record<number, number[]> = {};

    Object.entries(obj).forEach(([key, value]) => {
      if (typeof value === "string") {
        // Lấy ký tự đầu tiên trước dấu '-' và chuyển thành số
        const questionId = Number(key.split("-")[0]);

        // Nếu questionId đã tồn tại trong result, gộp value vào mảng (dưới dạng số)
        if (result[questionId]) {
          result[questionId].push(Number(value));
        } else {
          // Nếu chưa tồn tại, tạo mới với một mảng chứa value (dưới dạng số)
          result[questionId] = [Number(value)];
        }
      }
    });

    // Chuyển result từ object thành array và đổi tên thành answer
    return Object.entries(result).map(([questionId, answers]) => ({
      questionId: Number(questionId), // Chuyển questionId thành số
      answer: answers,
    }));
  };

  const submitForm = async () => {
    // Submit form to the server
    const formData = new FormData(form.current!);

    // Chuyển FormData thành một object
    const formValues = Object.fromEntries(formData.entries());

    const userAns = transformObjectToArray(formValues);

    const data = await submitExam(quiz.id, userAns);
    setIsSubmit(true);
    setUserAnswer(userAns);
    setScore(data.score);
    setQuizAnswer(data.quiz);
  };

  function getIsCorrectForAnswer(answerId: number): boolean | undefined {
    // Dùng flatMap để kết hợp tất cả câu trả lời từ mọi câu hỏi vào một mảng duy nhất
    const answer = quizAnswer
      ?.flatMap((question) => question.listAnswer)
      .find((a) => a.id === answerId); // Tìm câu trả lời theo answerId

    return answer?.isCorrect; // Trả về isCorrect nếu tìm thấy, undefined nếu không
  }

  function getCorrectQuestionTotal(): number {
    if (!quizAnswer || !userAnswer) return 0;
    const result = quizAnswer?.reduce((total, question) => {
      return question.listAnswer.filter((ans) => ans.isCorrect).length ===
        userAnswer?.find((ans) => ans.questionId === question.id)?.answer.length
        ? ++total
        : total;
    }, 0);

    return result;
  }

  function getInCorrectQuestionTotal(): number | undefined {
    if (!quizAnswer) return 0;

    return quizAnswer?.length - getCorrectQuestionTotal();
  }

  function isCorrectQuestion(questionId: number): boolean {
    return (
      quizAnswer
        ?.find((question) => question.id === questionId)
        ?.listAnswer.filter((ans) => ans.isCorrect).length ===
      userAnswer?.find((ans) => ans.questionId === questionId)?.answer.length
    );
  }
  useEffect(() => {
    // Intercept beforeunload event
    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault();
      event.returnValue = "";
    };

    // Disable F5 and Ctrl+R
    const disableRefresh = (event: KeyboardEvent) => {
      if (event.key === "F5" || (event.ctrlKey && event.key === "r")) {
        event.preventDefault();
        alert("Page refresh is disabled during the exam.");
      }
    };

    window.addEventListener("beforeunload", handleBeforeUnload);
    window.addEventListener("keydown", disableRefresh);

    return () => {
      window.removeEventListener("beforeunload", handleBeforeUnload);
      window.removeEventListener("keydown", disableRefresh);
    };
  }, []);

  return (
    <div className="py-10 lg:flex gap-10">
      <div className="flex flex-col gap-10 flex-1">
        <div>
          <div className="flex items-center gap-6">
            <p className="text-left">Examination: </p>
            <div className="shadow-custom rounded-xl max-w-80 px-6 py-3 flex items-center justify-center">
              <span className="line-clamp-2">{quiz.title}</span>
            </div>
          </div>

          <div className="flex items-center gap-6 mt-6">
            <p className="text-left">Subject: </p>
            <div className="shadow-custom rounded-xl w-fit px-6 py-3 flex items-center justify-center gap-x-3">
              <img
                className="w-16"
                src={
                  categoryList.find(
                    (ct) =>
                      ct.title.trim().replace(/\s+/g, "_").toUpperCase() ===
                      quiz.category
                  )?.imageUrl
                }
                alt="category_image"
              />
              <span className="capitalize">
                {quiz.category.toLocaleLowerCase()}
              </span>
            </div>
          </div>
        </div>

        {/* Time left */}
        <div className="lg:hidden">
          {isSubmit ? (
            <div className="flex gap-10 justify-center items-center">
              <div className="flex flex-col items-center w-fit gap-2">
                <img
                  src={images.score_rank}
                  className="w-8 aspect-square"
                  alt=""
                />
                <p>
                  Score: <sup>{score}</sup>/<sub>10</sub>
                </p>
              </div>

              <div className="flex flex-col items-center w-fit gap-2">
                <img
                  src={images.accuracy}
                  className="w-8 aspect-square"
                  alt=""
                />
                <p>Accuracy: {Math.round((score / 10) * 100)}%</p>
              </div>
            </div>
          ) : (
            <div className="flex flex-col gap-6">
              <div className="flex justify-between items-center">
                <p>Time Left:</p>
                <Countdown
                  date={targetDate}
                  renderer={({ hours, minutes, seconds, completed }) => {
                    if (completed) submitForm();
                    return (
                      <div
                        className={`${
                          hours > 0 ? "min-w-24" : "min-w-20"
                        } bg-primary text-white px-4 py-2 rounded-xl text-center`}
                      >
                        {completed
                          ? "Time's up!!!"
                          : `${
                              hours > 0 ? `${hours}:` : ""
                            }${minutes}:${seconds}`}
                      </div>
                    );
                  }}
                />
              </div>
              <ProgressBar
                height="10px"
                bgColor="#27b489"
                baseBgColor={"#cbd5e1"}
                completed={new Set(totalAns).size}
                customLabel={" "}
                maxCompleted={exams.length}
              />
              <p className="text-left">
                Tiến độ làm bài: {`${new Set(totalAns).size}/${exams.length}`}
              </p>
            </div>
          )}
        </div>

        {/* quest form */}
        {quiz.listQuestion.length !== 0 && (
          <form ref={form} onSubmit={(e) => handleSubmit(e)}>
            {exams.map((question, examsIndex) => (
              <div
                key={examsIndex}
                className={`${examsIndex === page ? "block" : "hidden"}`}
              >
                <div className="flex flex-col gap-6">
                  <p className="text-left">Question {examsIndex + 1}</p>
                  <p className="text-left">{question.content}</p>
                  {question.files.length > 0 && (
                    <QuizMedia files={question.files} />
                  )}
                  <ol className="flex pb-1 flex-col gap-10 group list-[upper-alpha]">
                    {question.listAnswer.map((answer, ansIndex) => (
                      <div key={ansIndex}>
                        <label
                          htmlFor={`${examsIndex}-${ansIndex}`}
                          className="flex gap-x-3 items-center"
                        >
                          <input
                            type="checkbox"
                            name={`${question.id}-${answer.id}`}
                            className="peer !hidden"
                            disabled={isSubmit}
                            value={answer.id}
                            id={`${examsIndex}-${ansIndex}`}
                            onChange={(e) => handleAnswerChange(e, examsIndex)}
                          />
                          <li
                            className={`peer-checked:bg-primary flex-1 peer-checked:text-white rounded-lg cursor-pointer hover:bg-slate-300 hover:text-white  shadow-custom p-4 list-inside text-left ${
                              isSubmit
                                ? "pointer-events-none peer-checked:bg-slate-300 peer-checked:text-white"
                                : ""
                            }`}
                          >
                            {answer.content}
                          </li>
                          {isSubmit ? (
                            <>
                              {answer?.id &&
                              getIsCorrectForAnswer(answer.id) ? (
                                <img
                                  src={images.correct_box}
                                  alt=""
                                  className={`w-7 aspect-square block`}
                                />
                              ) : (
                                <img
                                  src={images.incorrect_box}
                                  alt=""
                                  className={`hidden w-7 aspect-square peer-checked:block`}
                                />
                              )}
                            </>
                          ) : (
                            ""
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
              {!isSubmit && (
                <button
                  type="submit"
                  className="border-none min-w-32 flex items-center justify-center gap-2 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white "
                >
                  Submit
                  <FaLocationArrow />
                </button>
              )}

              <div className="flex flex-wrap gap-x-3">
                <button
                  type="button"
                  onClick={() => goToPage(page - 1)}
                  className={`${
                    page > 0 ? "block" : "hidden"
                  } border-none flex items-center justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white `}
                >
                  <RiArrowGoBackFill />
                  Previous
                </button>
                <button
                  type="button"
                  onClick={() => goToPage(page + 1)}
                  className={`${
                    page < exams.length - 1 ? "block" : "hidden"
                  } border-none flex items-center justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white `}
                >
                  Next
                  <RiArrowGoForwardFill />
                </button>
              </div>
            </div>
          </form>
        )}
      </div>

      {/* Time left */}
      <div className="hidden lg:block">
        {isSubmit ? (
          <div className="flex gap-10 justify-center items-center mb-6">
            <div className="flex flex-col items-center w-fit gap-2">
              <img
                src={images.score_rank}
                className="w-8 aspect-square"
                alt=""
              />
              <p>
                Score: <sup>{score}</sup>/<sub>10</sub>
              </p>
            </div>

            <div className="flex flex-col items-center w-fit gap-2">
              <img src={images.accuracy} className="w-8 aspect-square" alt="" />
              <p>Accuracy: {Math.round((score / 10) * 100)}%</p>
            </div>
          </div>
        ) : (
          <div className="flex flex-col gap-6 mb-6">
            <div className="flex justify-between items-center">
              <p>Time Left:</p>
              <Countdown
                date={targetDate}
                renderer={({ hours, minutes, seconds, completed }) => {
                  if (completed) submitForm();
                  return (
                    <div
                      className={`${
                        hours > 0 ? "min-w-24" : "min-w-20"
                      } bg-primary text-white px-4 py-2 rounded-xl text-center`}
                    >
                      {completed
                        ? "Time's up!!!"
                        : `${
                            hours > 0 ? `${hours}:` : ""
                          }${minutes}:${seconds}`}
                    </div>
                  );
                }}
              />
            </div>
            <ProgressBar
              height="10px"
              baseBgColor={"#cbd5e1"}
              bgColor="#27b489"
              completed={new Set(totalAns).size}
              customLabel={" "}
              maxCompleted={exams.length}
            />
            <p className="text-left">
              Tiến độ làm bài: {`${new Set(totalAns).size}/${exams.length}`}
            </p>
          </div>
        )}

        <div
          className={`mt-3 flex flex-col gap-8 ${
            isSubmit ? "flex-col-reverse" : ""
          }`}
        >
          <div>
            <ul className="grid grid-cols-4 w-fit gap-6">
              {exams.map((question, index) => (
                <li
                  key={index}
                  onClick={() => {
                    goToPage(index);
                  }}
                  className={`${
                    !isSubmit
                      ? totalAns.includes(index)
                        ? "bg-primary text-white"
                        : "hover:bg-slate-300 hover:text-white"
                      : ""
                  } shadow-custom w-10 grid place-items-center rounded-xl cursor-pointer aspect-square ${
                    isSubmit
                      ? `${
                          isCorrectQuestion(question.id!)
                            ? "bg-primary text-white"
                            : "bg-red-500 opacity-80 text-white"
                        }`
                      : ""
                  }`}
                >
                  {index + 1}
                </li>
              ))}
              {/* {Array.from({ length: exams.length }, (_, index) => (
                <li
                  key={index}
                  onClick={() => {
                    goToPage(index);
                  }}
                  className={`${
                    totalAns.includes(index)
                      ? isSubmit
                        ? ""
                        : "bg-primary text-white"
                      : isSubmit
                      ? "bg-red-500 opacity-80 text-white"
                      : "hover:bg-slate-300 hover:text-white"
                  } shadow-custom w-10 grid place-items-center rounded-xl cursor-pointer aspect-square `}
                >
                  {index + 1}
                </li>
              ))} */}
            </ul>
          </div>

          <div
            className={`flex flex-col gap-4 ${
              isSubmit ? "!flex-row gap-8" : ""
            }`}
          >
            {isSubmit ? (
              <>
                <div className="flex gap-3 items-center">
                  <div className="flex flex-col gap-1">
                    <p>Correct</p>
                    <div className="flex gap-3 items-center">
                      <img
                        src={images.correct_box}
                        className="w-7 aspect-square"
                        alt=""
                      />
                      {getCorrectQuestionTotal()}
                    </div>
                  </div>
                </div>
                <div className="flex gap-3 items-center">
                  <div className="flex flex-col gap-1">
                    <p>Incorrect</p>
                    <div className="flex gap-3 items-center">
                      <img src={images.incorrect_box} className="w-7" alt="" />
                      {getInCorrectQuestionTotal()}
                    </div>
                  </div>
                </div>
              </>
            ) : (
              <>
                <div className="flex gap-3 items-center">
                  <span className="block rounded-lg w-6 aspect-square bg-primary"></span>
                  <p>Attempted</p>
                </div>
                <div className="flex gap-3 items-center">
                  <span className="block rounded-lg w-6 aspect-square shadow-custom"></span>
                  <p>Pending</p>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Exam;
