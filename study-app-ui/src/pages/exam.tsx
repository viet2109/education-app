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
import Swal from "sweetalert2";
import {images} from "../assets/images";

interface ExamProps {}

const Exam: FC<ExamProps> = () => {
    const [page, setPage] = useState(0);
    const [totalAns, setTotalAns] = useState<Set<number>>(new Set());
    const form = useRef<HTMLFormElement>(null);

    const targetDate = useMemo(() => Date.now() + 1000 * 3600 * 3, []);

    const examList = [
        {
            question: " When a worksheet is deleted from a workbook, it is",
            answer: [
                "permanently deleted",
                "moved to the recycle bin",
                "saved in the default location",
                "copied to the recycle bin",
            ],
        },
        {
            question:
                "Which of the followmg agricultural raw materials would a glue-processing industry require? ",
            answer: ["Cotton", "Coconut", "Cassava", "Kola"],
        }
    ];

    const exams = useMemo(() => examList, []);

    const handleAnswerChange = useCallback((examsIndex: number) => {
        setTotalAns((prev) => {
            const newSet = new Set(prev);
            newSet.add(examsIndex);
            return newSet;
        });
    }, []);

    const goToPage = useCallback((newPage: number) => {
        setPage(newPage);
    }, []);

    function handleSubmit(e: FormEvent<HTMLFormElement>): void {
        e.preventDefault();
        const text =
            totalAns.size === exams.length
                ? "Are you sure you want to submit the exam?"
                : `You have ${
                    exams.length - totalAns.size
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

    const submitForm = () => {
        // Submit form to the server
        alert("Submit");
        const formData = new FormData(form.current!);

        // Chuyển FormData thành một object
        const formValues = Object.fromEntries(formData.entries());

        console.log(formValues);
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
                        <p className="text-left">Examination: </p>
                        <div className="shadow-custom rounded-xl w-fit px-6 py-3 flex items-center justify-center">
                            <img
                                className="w-7"
                                src={images.category}
                                alt="category_image"
                            />
                            <span className="uppercase">WASSCE/GCE</span>
                        </div>
                    </div>

                    <div className="flex items-center gap-6 mt-6">
                        <p className="text-left">Subject: </p>
                        <div className="shadow-custom rounded-xl w-fit px-6 py-3 flex items-center justify-center">
                            <img
                                className="w-7"
                                src={images.category}
                                alt="category_image"
                            />
                            <span className="capitalize">Computer Studies</span>
                        </div>
                    </div>
                </div>
                {/* Time left */}
                <div className="lg:hidden">
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
                            bgColor="#27b489"
                            baseBgColor={"#cbd5e1"}
                            completed={totalAns.size}
                            customLabel={" "}
                            maxCompleted={exams.length}
                        />
                        <p className="text-left">
                            Tiến độ làm bài: {`${totalAns.size}/${exams.length}`}
                        </p>
                    </div>
                </div>

                {/* quest form */}
                <form ref={form} onSubmit={(e) => handleSubmit(e)}>
                    {exams.map((question, examsIndex) => (
                        <div
                            key={examsIndex}
                            className={`${examsIndex === page ? "block" : "hidden"}`}
                        >
                            <div className="flex flex-col gap-6">
                                <p className="text-left">Question {examsIndex + 1}</p>
                                <p className="text-left">{question.question}</p>
                                <ol className="flex px-1 pb-1 flex-col gap-5 group list-[upper-alpha]">
                                    {question.answer.map((answer, ansIndex) => (
                                        <div key={ansIndex}>
                                            <label htmlFor={`${examsIndex}-${ansIndex}`}>
                                                <input
                                                    type="radio"
                                                    name={`${examsIndex}`}
                                                    className="peer hidden"
                                                    value={answer}
                                                    id={`${examsIndex}-${ansIndex}`}
                                                    onChange={() => handleAnswerChange(examsIndex)}
                                                />
                                                <li className="peer-checked:bg-primary peer-checked:text-white rounded-lg cursor-pointer hover:bg-slate-300 hover:text-white transition-all duration-300 shadow-custom p-4 list-inside text-left">
                                                    {answer}
                                                </li>
                                            </label>
                                        </div>
                                    ))}
                                </ol>
                            </div>
                        </div>
                    ))}

                    <div className="mt-6 flex justify-between">
                        <button
                            type="submit"
                            className="border-none min-w-32 flex items-center justify-center gap-2 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white transition-all duration-300"
                        >
                            Submit
                            <FaLocationArrow />
                        </button>
                        <div className="flex flex-wrap gap-x-3">
                            <button
                                type="button"
                                onClick={() => goToPage(page - 1)}
                                className={`${
                                    page > 0 ? "block" : "hidden"
                                } border-none flex items-center justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white transition-all duration-300`}
                            >
                                <RiArrowGoBackFill />
                                Previous
                            </button>
                            <button
                                type="button"
                                onClick={() => goToPage(page + 1)}
                                className={`${
                                    page < exams.length - 1 ? "block" : "hidden"
                                } border-none flex items-center justify-center gap-3 min-w-32 bg-slate-300 p-4 rounded-lg hover:bg-primary hover:text-white transition-all duration-300`}
                            >
                                Next
                                <RiArrowGoForwardFill />
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            {/* Time left */}
            <div className="hidden lg:block">
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
                        completed={totalAns.size}
                        customLabel={" "}
                        maxCompleted={exams.length}
                    />
                    <p className="text-left">
                        Tiến độ làm bài: {`${totalAns.size}/${exams.length}`}
                    </p>
                </div>

                <div>
                    <ul className="grid grid-cols-4 w-fit gap-6">
                        {Array.from({ length: exams.length }, (_, index) => (
                            <li
                                key={index}
                                onClick={() => {
                                    goToPage(index);
                                }}
                                className={`${
                                    totalAns.has(index)
                                        ? "bg-primary text-white"
                                        : "hover:bg-slate-300 hover:text-white"
                                } shadow-custom w-10 grid place-items-center rounded-xl cursor-pointer  transition-all duration-200 aspect-square`}
                            >
                                {index + 1}
                            </li>
                        ))}
                    </ul>
                </div>

                <div className="flex mt-8 flex-col gap-4">
                    <div className="flex gap-3 items-center">
                        <span className="block rounded-lg w-6 aspect-square bg-primary"></span>
                        <p>Attempted</p>
                    </div>
                    <div className="flex gap-3 items-center">
                        <span className="block rounded-lg w-6 aspect-square shadow-custom"></span>
                        <p>Pending</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Exam;
