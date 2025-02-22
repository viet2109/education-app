import ProgressBar from "@ramonak/react-progress-bar";
import { useQuery } from "@tanstack/react-query";
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
import { useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import { getAllCategories, submitExam } from "../api/quiz";
import QuizMedia from "../components/quizMedia";
import { DEFAULT_SLATE_TIME } from "../constant";
import { getLocalDateTime } from "../helper/formatLocalDate";
import { RootState } from "../redux/store";
import { Category, Quiz } from "../types";
import routers from "../configs/routers";
import ImageWithSkeleton from "../components/imageWithSkeleton";

interface ExamProps {}
type InputObject = Record<string, FormDataEntryValue>;

const Exam: FC<ExamProps> = () => {
  const quiz: Quiz = useLocation().state;
  const [page, setPage] = useState(0);
  const [totalAns, setTotalAns] = useState<Array<number>>([]);
  const form = useRef<HTMLFormElement>(null);
  const [startedAt] = useState(getLocalDateTime(new Date()));
  const navigate = useNavigate();
  const targetDate = useMemo(() => Date.now() + 1000 * quiz.duration, []);
  const user = useSelector((state: RootState) => state.auth.user);
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
        ? "Bạn có chắc chắn muốn nộp bài thi không?"
        : `Bạn còn ${
            exams.length - new Set(totalAns).size
          } câu hỏi chưa trả lời. Bạn có chắc chắn muốn nộp bài thi không?`;
    Swal.fire({
      title: "Xác nhận nộp bài",
      text,
      icon: "question",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Có, nộp bài!",
      background: "#fff",
    }).then((result) => {
      if (result.isConfirmed) {
        submitForm();
      }
    });
  }

  const transformObjectToUserAnswers = (
    obj: InputObject
  ): Record<number, number[]> => {
    const result: Record<number, number[]> = {};

    Object.entries(obj).forEach(([key, value]) => {
      if (typeof value === "string") {
        // Lấy questionId từ key (ký tự trước dấu '-')
        const questionId = Number(key.split("-")[0]);

        // Đảm bảo questionId hợp lệ và là số
        if (!isNaN(questionId)) {
          // Nếu questionId đã tồn tại, thêm value vào mảng
          if (result[questionId]) {
            result[questionId].push(Number(value));
          } else {
            // Nếu chưa tồn tại, khởi tạo mảng với value
            result[questionId] = [Number(value)];
          }
        }
      }
    });

    // Trả về object theo kiểu Record<number, number[]>
    return result;
  };

  const submitForm = async () => {
    const finishedAt = getLocalDateTime(new Date());

    // Submit form to the server
    const formData = new FormData(form.current!);

    // Chuyển FormData thành một object
    const formValues = Object.fromEntries(formData.entries());

    const userAnswers = transformObjectToUserAnswers(formValues);

    try {
      const data = await submitExam(quiz.id, {
        userAnswers,
        userId: user?.id || "",
        startedAt,
        finishedAt,
      });
      navigate(routers.examHistoryDetail.replace(":id", data.id.toString()));
    } catch (error: any) {
      console.error(error);
    }
  };

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
            <p className="text-left">Bài thi: </p>
            <div className="shadow-custom rounded-xl max-w-80 px-6 py-3 flex items-center justify-center">
              <span className="line-clamp-2">{quiz.title}</span>
            </div>
          </div>

          <div className="flex items-center gap-6 mt-6">
            <p className="text-left">Môn học: </p>
            <div className="shadow-custom rounded-xl w-fit px-6 py-3 flex items-center justify-center gap-x-3">
              <ImageWithSkeleton
                className="w-auto"
                imgClass="w-16"
                skeletonClass="!h-12 !w-16"
                src={
                  categoryList.find(
                    (ct) =>
                      ct.title.trim().replace(/\s+/g, "_").toUpperCase() ===
                      quiz.category
                  )?.imageUrl || ""
                }
                alt="category_image"
              />

              <span className="capitalize">
                {quiz.category.toLocaleLowerCase().replace("_", " ")}
              </span>
            </div>
          </div>
        </div>

        {/* Time left */}
        <div className="lg:hidden">
          <div className="flex flex-col gap-6">
            <div className="flex justify-between items-center">
              <p>Thời gian còn lại:</p>
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
                  <p className="text-left">Câu hỏi {examsIndex + 1}</p>
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
                            value={answer.id}
                            id={`${examsIndex}-${ansIndex}`}
                            onChange={(e) => handleAnswerChange(e, examsIndex)}
                          />
                          <li
                            className={`peer-checked:bg-primary flex-1 peer-checked:text-white rounded-lg cursor-pointer hover:bg-slate-300 hover:text-white  shadow-custom p-4 list-inside text-left
                              `}
                          >
                            {answer.content}
                          </li>
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
                type="submit"
                className="border-none min-w-32 flex items-center justify-center gap-2 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white "
              >
                Nộp bài
                <FaLocationArrow />
              </button>

              <div className="flex flex-wrap gap-x-3">
                <button
                  type="button"
                  onClick={() => goToPage(page - 1)}
                  className={`${
                    page > 0 ? "block" : "hidden"
                  } border-none flex items-center justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white `}
                >
                  <RiArrowGoBackFill />
                  Câu hỏi trước
                </button>
                <button
                  type="button"
                  onClick={() => goToPage(page + 1)}
                  className={`${
                    page < exams.length - 1 ? "block" : "hidden"
                  } border-none flex items-center justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white `}
                >
                  Câu hỏi tiếp theo
                  <RiArrowGoForwardFill />
                </button>
              </div>
            </div>
          </form>
        )}
      </div>

      {/* Time left */}
      <div className="hidden lg:block">
        <div className="flex flex-col gap-6 mb-6">
          <div className="flex justify-between items-center">
            <p>Thời gian còn lại:</p>
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
                      : `${hours > 0 ? `${hours}:` : ""}${minutes}:${seconds}`}
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

        <div className={`mt-3 flex flex-col gap-8`}>
          <div>
            <ul className="grid grid-cols-4 w-fit gap-6">
              {exams.map((_, index) => (
                <li
                  key={index}
                  onClick={() => {
                    goToPage(index);
                  }}
                  className={`${
                    totalAns.includes(index)
                      ? "bg-primary text-white"
                      : "hover:bg-slate-300 hover:text-white"
                  } shadow-custom w-10 grid place-items-center rounded-xl cursor-pointer aspect-square 
                    `}
                >
                  {index + 1}
                </li>
              ))}
            </ul>
          </div>

          <div className={`flex flex-col gap-4`}>
            <div className="flex gap-3 items-center">
              <span className="block rounded-lg w-6 aspect-square bg-primary"></span>
              <p>Đã chọn</p>
            </div>
            <div className="flex gap-3 items-center">
              <span className="block rounded-lg w-6 aspect-square shadow-custom"></span>
              <p>Chưa chọn</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Exam;
