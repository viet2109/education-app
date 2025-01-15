import { useQuery } from "@tanstack/react-query";
import { useCallback, useEffect, useState } from "react";
import { BiArrowBack, BiPlus } from "react-icons/bi";
import { FaEdit } from "react-icons/fa";
import { FaRegTrashCan } from "react-icons/fa6";
import { useNavigate, useParams } from "react-router-dom";
import Select from "react-select";
import Swal from "sweetalert2";
import { createQuestion, deleteQuestion } from "../api/question";
import { fetchManageQuiz } from "../api/quiz";
import NoDataModel from "../components/noDataModel";
import Pagianate from "../components/paginate";
import QuestionModal from "../components/questionModal";
import QuestionSidebar from "../components/questionSidebar";
import { formatLocalDate } from "../helper/formatLocalDate";
import { QuestionResponseDto } from "../types";

function SettingQuizDetails() {
  const { id } = useParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSidebarOpen, setSidebarOpen] = useState(false);
  const navigate = useNavigate();
  const [selectedQuestion, setSelectedQuestion] =
    useState<QuestionResponseDto | null>(null);

  const handleCreateQuestion = useCallback(() => {
    Swal.fire({
      title: "Select Question Creation Method",
      html: `<ul class="space-y-4 mb-4">
                      <li>
                          <label for="m-1" class="inline-flex items-center justify-between w-full p-5 text-gray-900 bg-white border border-gray-300 rounded-lg cursor-pointer has-[:checked]:border-primary has-[:checked]:text-primary hover:text-gray-900 hover:bg-gray-100">                           
                          <input type="radio" id="m-1" name="method" value="manually" class="!hidden peer" required />
                              
                          <div class="block">
                                  <div class="w-full text-lg text-left font-semibold">Manual Creation</div>
                                  <div class="w-full text-gray-500 text-sm text-left">Enter each question and answer directly.</div>
                              </div>
                              <svg class="w-4 h-4 min-w-4 peer-checked:text-primary ms-3 rtl:rotate-180 text-gray-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M1 5h12m0 0L9 1m4 4L9 9"/></svg>
                          </label>
                      </li>
                      <li>
                          <label for="m-2" class="inline-flex items-center justify-between w-full p-5 text-gray-900 bg-white border border-gray-300 rounded-lg cursor-pointer has-[:checked]:border-primary bg-red has-[:checked]:text-primary hover:text-gray-900 hover:bg-gray-100">                           
                          <input type="radio" id="m-2" name="method" value="import" class="!hidden peer" />
                              
                          <div class="block">
                                  <div class="w-full text-lg text-left font-semibold">Choose from Question Bank</div>
                                  <div class="w-full text-gray-500 text-sm text-left">Select questions from the question bank to create your quiz easily. You can also upload a file containing your questions to automatically generate a quiz based on the content.</div>
                              </div>
                              <svg class="w-4 h-4 min-w-4 peer-checked:text-primary ms-3 rtl:rotate-180 text-gray-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10"><path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M1 5h12m0 0L9 1m4 4L9 9"/></svg>
                          </label>
                      </li>
                  </ul>`,

      preConfirm: () => {
        const selectedValue = document.querySelector<HTMLInputElement>(
          'input[name="method"]:checked'
        )?.value;
        if (!selectedValue) {
          Swal.showValidationMessage("Please select an option to continue");
          return false;
        }
        return selectedValue;
      },
      showCancelButton: true,
      icon: "question",
      confirmButtonText: "Next step",
      cancelButtonColor: "#ef4444",
      confirmButtonColor: "#27b489",
    }).then((result) => {
      if (result.isConfirmed) {
        if (result.value === "manually") {
          setSelectedQuestion(null);
          toggleModal(setIsModalOpen, true);
        } else if (result.value === "import") {
          setSidebarOpen(true);
        }
      }
    });
  }, []);

  const [categories] = useState([
    { value: "MATHEMATICS", label: "Mathematics" },
    { value: "LITERATURE", label: "Literature" },
    { value: "NATURAL_SCIENCES", label: "Natural Sciences" },
    { value: "SOCIAL_SCIENCES", label: "Social Sciences" },
    { value: "FOREIGN_LANGUAGES", label: "Foreign Languages" },
    { value: "INFORMATION_TECHNOLOGY", label: "Information Technology" },
    { value: "ART", label: "Art" },
    { value: "ECONOMICS", label: "Economics" },
    { value: "HEALTH", label: "Health" },
    { value: "SPORTS", label: "Sports" },
    { value: "OTHERS", label: "Others" },
  ]);

  const toggleModal = (
    setModalOpen: React.Dispatch<React.SetStateAction<boolean>>,
    isOpen: boolean
  ) => {
    setModalOpen(isOpen);
  };

  const handleQuestionSubmit = async (question: QuestionResponseDto) => {
    question.examId = Number(id);

    if (selectedQuestion) {
      console.log(question);
    } else {
      Swal.fire({
        title: "Creating question...",
        allowOutsideClick: false,
        didOpen: () => {
          Swal.showLoading();
        },
      });

      try {
        await createQuestion(question);
        refetch();
        Swal.fire({
          icon: "success",
          title: "Quiz create successfully",
          showConfirmButton: true,
          confirmButtonText: "OK",
        });
      } catch (error) {
        console.log(error);
        Swal.fire({
          icon: "error",
          title: "Creating failed",
          text: "An error occurred while creating the question. Please try again.",
          confirmButtonColor: "#ef4444",
        });
      }
    }
  };

  const { data, isLoading, refetch } = useQuery({
    queryKey: ["quizzes"],
    queryFn: () => fetchManageQuiz(Number(id)),
    retry: false,
    enabled: false,
  });

  const [questions, setQuestions] = useState<QuestionResponseDto[]>([]);

  const handleDelete = async (id: number) => {
    const result = await Swal.fire({
      title: "Confirm delete question",
      icon: "question",
      text: `Do you want to delete question ${id}`,
      showCancelButton: true,
      cancelButtonColor: "#ef4444",
      confirmButtonColor: "#27b489",
    });

    if (result.isConfirmed) {
      Swal.fire({
        title: "Deleting question...",
        allowOutsideClick: false,
        didOpen: () => {
          Swal.showLoading();
        },
      });

      try {
        await deleteQuestion(id);
        const updatedQuestions = questions.filter(
          (question) => question.id !== id
        );
        setQuestions(updatedQuestions);

        // Tính tổng số câu hỏi còn lại và cập nhật trang hiện tại
        const totalItems = updatedQuestions.length;
        const totalPages = Math.ceil(totalItems / itemsPerPage);

        setCurrentPage((prevPage) => {
          if (totalItems === 0) {
            return 0;
          } else if (prevPage >= totalPages) {
            return totalPages - 1;
          }
          return prevPage;
        });

        Swal.fire({
          icon: "success",
          title: "Quiz deleting successfully",
          showConfirmButton: true,
          confirmButtonText: "OK",
        });
      } catch (error) {
        Swal.fire({
          icon: "error",
          title: "Deleting failed",
          text: "An error occurred while deleting the quiz. Please try again.",
          confirmButtonColor: "#ef4444",
        });
      }
    }
  };

  function toggleSidebar(): void {
    setSidebarOpen((prev) => !prev);
  }

  const [currentPage, setCurrentPage] = useState(0);

  const itemsPerPage = 4;

  useEffect(() => {
    if (id) {
      refetch();
    }
  }, [id, refetch]);

  useEffect(() => {
    if (data?.listQuestion) {
      setQuestions(data.listQuestion);
    }
  }, [data?.listQuestion]);

  return (
    <>
      <button
        className="btn-custom flex items-center gap-2 mb-3"
        onClick={() => navigate(-1)}
      >
        <BiArrowBack></BiArrowBack>
        <span>Back</span>
      </button>
      <div className="mb-3">
        <h2 className="text-primary text-lg">Manage detail quiz</h2>
      </div>
      {isLoading ? (
        <div className="h-4 w-1/4 bg-gray-300 rounded animate-pulse mb-4"></div>
      ) : (
        <h2>Quiz id: {data?.id}</h2>
      )}

      {/* form quiz */}
      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 md:gap-8 mt-3">
          {/* Skeleton for Title */}
          <div>
            <div className="h-4 w-1/4 bg-gray-300 rounded animate-pulse mb-2"></div>
            <div className="h-24 w-full bg-gray-300 rounded-md animate-pulse"></div>
          </div>

          {/* Skeleton for Category */}
          <div>
            <div className="h-4 w-1/4 bg-gray-300 rounded animate-pulse mb-2"></div>
            <div className="h-10 w-full bg-gray-300 rounded-md animate-pulse"></div>
          </div>

          {/* Skeleton for Duration */}
          <div>
            <div className="h-4 w-1/3 bg-gray-300 rounded animate-pulse mb-2"></div>
            <div className="h-10 w-full bg-gray-300 rounded-md animate-pulse"></div>
          </div>

          {/* Skeleton for Expiration Date */}
          <div>
            <div className="h-4 w-1/3 bg-gray-300 rounded animate-pulse mb-2"></div>
            <div className="h-10 w-full bg-gray-300 rounded-md animate-pulse"></div>
          </div>

          {/* Skeleton for Questions */}
          <div className="md:col-span-2">
            <div className="h-4 w-1/3 bg-gray-300 rounded animate-pulse mb-2"></div>
            <div className="h-10 w-40 bg-gray-300 rounded-md animate-pulse mb-4"></div>

            {/* Skeleton for List of Questions */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
              {[...Array(4)].map((_, index) => (
                <div
                  key={index}
                  className="h-24 w-full bg-gray-300 rounded-md animate-pulse"
                ></div>
              ))}
            </div>
          </div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 md:gap-8 *:flex *:flex-col *:gap-2 mt-3">
            {/* title */}
            <div>
              <label htmlFor="title" className="cursor-pointer">
                Title:{" "}
              </label>
              <textarea
                spellCheck={false}
                rows={4}
                className="border-2 w-full py-2 px-4 border-slate-400 outline-none rounded-md focus:border-primary"
                name="title"
                id="title"
                defaultValue={data?.title}
              ></textarea>
            </div>

            {/* category */}
            <div>
              <label htmlFor="">Category: </label>
              {data?.category && (
                <Select
                  options={categories}
                  defaultValue={categories.find((category) => {
                    return category.value === data.category;
                  })}
                  styles={{
                    control: (baseStyles) => ({
                      ...baseStyles,
                      borderWidth: "2px",
                      borderColor: "#94a3b8",
                      boxShadow: "none",
                      paddingLeft: "6px",
                      "&:focus-within": {
                        borderColor: "#27b489",
                        boxShadow: "0 0 0.2rem rgba(39, 180, 137, 1)",
                      },
                    }),
                  }}
                ></Select>
              )}
            </div>

            {/* duration */}
            <div>
              <label htmlFor="duration" className="cursor-pointer">
                Duration (calc by min):{" "}
              </label>
              <input
                type="number"
                id="duration"
                name="duration"
                min={1}
                defaultValue={data?.duration && Math.floor(data?.duration / 60)}
                className="border-2 p-1.5 pl-4 rounded-md outline-none focus:border-primary border-slate-400"
              />
            </div>

            {/* expirated */}
            <div>
              <label htmlFor="expirated" className="cursor-pointer">
                Expirated:
              </label>
              <input
                type="datetime-local"
                id="expirated"
                name="expirated"
                defaultValue={""}
                className="border-2 p-1.5 pl-4 rounded-md outline-none focus:border-primary border-slate-400"
              />
            </div>

            {/* question */}
            <div className="md:col-span-2">
              <label htmlFor="">Questions: </label>
              <button
                type="button"
                className="btn-custom w-fit flex items-center gap-1"
                onClick={handleCreateQuestion}
              >
                <BiPlus size={18} />
                <span>New question</span>
              </button>

              {/* list question */}
              {questions && questions.length > 0 ? (
                <ul className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 *:shadow-custom *:p-6 *:rounded-lg my-4 mb-0">
                  {questions
                    .slice(
                      currentPage * itemsPerPage,
                      currentPage * itemsPerPage + itemsPerPage
                    )
                    .map((question) => (
                      <li
                        key={question.id}
                        className="hover:bg-slate-300 hover:text-white group cursor-default transition-all duration-300"
                      >
                        <div className="before:block before:w-6 before:h-2 before:bg-primary before:rounded-full before:absolute before:-top-3 before:left-0 relative before:transition-all before:duration-300 group-hover:before:bg-white">
                          <div className="flex justify-between">
                            <span>Id: {question.id}</span>
                            <div className="flex gap-2">
                              <FaEdit
                                size={18}
                                className="hover:!text-opacity-65 !text-blue-500 transition-all duration-300 cursor-pointer"
                                onClick={() => {
                                  setSelectedQuestion((_prev) => question);
                                  toggleModal(setIsModalOpen, true);
                                }}
                              />
                              <FaRegTrashCan
                                size={18}
                                className="hover:!text-opacity-65 !text-red-500 transition-all duration-300 cursor-pointer"
                                onClick={() => {
                                  question.id && handleDelete(question.id);
                                }}
                              />
                            </div>
                          </div>
                          <p className="quiz-title line-clamp-1">
                            Title: {question.content}
                          </p>
                          <span className="text-sm italic text-gray-400 group-hover:text-white">
                            Last updated at:{" "}
                            {formatLocalDate(question.updatedAt)}
                          </span>
                        </div>
                      </li>
                    ))}
                </ul>
              ) : (
                <NoDataModel
                  title="This quiz has no question yet"
                  customBtn={<></>}
                />
              )}

              {questions && questions.length > 0 && (
                <Pagianate
                  initialPage={currentPage}
                  onPageChange={(number) => {
                    setCurrentPage(number);
                  }}
                  itemsLength={questions.length}
                  numberItemOnPage={itemsPerPage}
                />
              )}
            </div>
          </div>
          <div className="grid place-items-center mt-8">
            <button type="button" className="btn-custom">
              Save change
            </button>
          </div>
        </>
      )}

      {/* create question form */}
      <QuestionModal
        isOpen={isModalOpen}
        onClose={() => toggleModal(setIsModalOpen, false)}
        onSubmit={handleQuestionSubmit}
        initialQuestion={selectedQuestion || undefined}
      />

      {/* question banking */}
      {id && (
        <QuestionSidebar
          handleCreate={() => {
            console.log("quiz management");
            refetch();
          }}
          examId={Number(id)}
          isOpen={isSidebarOpen}
          toggleSidebar={toggleSidebar}
        />
      )}
    </>
  );
}

export default SettingQuizDetails;
