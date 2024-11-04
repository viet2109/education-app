import {
  useState,
  useCallback,
  useEffect,
  Ref,
  RefObject,
  useRef,
  ChangeEvent,
  FormEvent,
} from "react";
import { useParams } from "react-router-dom";
import { deleteQuiz, fetchQuiz, fetchQuizzes } from "../api/quiz";
import { Media, QuestionResponseDto, Quiz } from "../types";
import { useQuery } from "@tanstack/react-query";
import { DEFAULT_SLATE_TIME } from "../constant";
import NoDataModel from "../components/noDataModel";
import { BiArrowBack, BiPlus } from "react-icons/bi";
import Select from "react-select";
import { FaEdit } from "react-icons/fa";
import { FaEye, FaRegTrashCan } from "react-icons/fa6";
import { TbFileExport } from "react-icons/tb";
import routers from "../configs/routers";
import Swal from "sweetalert2";
import { deleteQuestion } from "../api/question";
import FormCreateQuestion, {
  Answer,
  Question,
} from "../components/questionModal";
import QuestionModal from "../components/questionModal";
import FileModal from "../components/fileModal";
import MediaManager from "../components/fileModal";
import MediaManagerModal from "../components/fileModal";
import QuestionSidebar from "../components/questionSidebar";

function SettingQuizDetails() {
  const { id } = useParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isFileModalOpen, setIsFileModalOpen] = useState(false);
  const [isSidebarOpen, setSidebarOpen] = useState(false);
  const [selectedMedia, setSelectedMedia] = useState<Media | null>(null);
  const [selectedQuestion, setSelectedQuestion] = useState<Question | null>(
    null
  );

  const handleCreateQuestion = () => {
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
    }).then((result) => {
      if (result.isConfirmed) {
        if (result.value === "manually") {
          // navigate(routers.home);
          toggleModal(setIsModalOpen, true);
        } else if (result.value === "import") {
          setSidebarOpen(true);
        }
      }
    });
  };

  const handleSelectMedia = (media: Media) => {
    setSelectedMedia(media);
    toggleModal(setIsFileModalOpen, false);
  };

  const toggleModal = (
    setModalOpen: React.Dispatch<React.SetStateAction<boolean>>,
    isOpen: boolean
  ) => {
    setModalOpen(isOpen);
  };

  const [questions, setQuestions] = useState<QuestionResponseDto[]>([
    {
      id: 1,
      content: "Question 1: What is React?",
      listAnswer: [
        {
          id: 1,
          content: "A JavaScript library for building user interfaces.",
          files: [],
          isCorrect: true,
        },
        {
          id: 2,
          content: "A framework for building mobile apps.",
          files: [],
          isCorrect: false,
        },
      ],
      examId: 101,
      files: [],
    },
    {
      id: 2,
      content: "Question 2: What is a component?",
      listAnswer: [
        {
          id: 3,
          content: "A part of a user interface.",
          files: [],
          isCorrect: true,
        },
        { id: 4, content: "A server-side logic.", files: [], isCorrect: false },
      ],
      examId: 101,
      files: [],
    },
  ]);
  const handleQuestionSubmit = (question: Question) => {
    console.log("Submitted question:", question);
    console.log("Submitted answers:", question.listAnswer);
  };

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ["quizzes"],
    queryFn: () => fetchQuiz(Number(id)),
    staleTime: DEFAULT_SLATE_TIME,
    retry: false,
    enabled: false,
  });

  const categories = [
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
  ];

  const handleCreateOrUpdate = (question: QuestionResponseDto) => {
    if (question.id) {
      // Update existing question
      setQuestions(questions.map((q) => (q.id === question.id ? question : q)));
    } else {
      // Create new question
      setQuestions([...questions, { ...question, id: Date.now() }]); // Using Date.now() for unique ID
    }
    // setEditingQuestion(null);
  };

  const handleEdit = (question: QuestionResponseDto) => {
    // setEditingQuestion(question);
  };

  const handleDelete = (id: number) => {
    setQuestions(questions.filter((q) => q.id !== id));
  };

  function handleOpenEditQuestionModal() {
    throw new Error("Function not implemented.");
  }

  function toggleSidebar(): void {
    setSidebarOpen((prev) => !prev);
  }

  // useEffect(() => {
  //   refetch();
  // }, [refetch]);

  return (
    <>
      <button className="btn-custom flex items-center gap-2 mb-3">
        <BiArrowBack></BiArrowBack>
        <span>Back {data?.id}</span>
      </button>
      <div className="mb-3">
        <h2 className="text-primary text-lg">Manage detail quiz</h2>
      </div>
      <h2>Quiz id: 1</h2>

      {/* form quiz */}
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
            defaultValue={`Lorem ipsum dolor sit amet consectetur adipisicing elit. Numquam ex eius quos voluptatum officia amet quod cupiditate laboriosam illum vero accusamus velit doloremque rem quia, deserunt maxime pariatur veritatis nemo.`}
          ></textarea>
        </div>

        {/* category */}
        <div>
          <label htmlFor="">Category: </label>
          <Select
            options={categories}
            defaultValue={categories[0]}
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
            defaultValue={60}
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
          <table className="w-full text-left">
            <thead className="border-b-2">
              <tr className="*:pb-4 *:pt-0.5 *:*:line-clamp-1">
                <th>
                  <span>Id</span>
                </th>
                <th>
                  <span>Content</span>
                </th>
                <th className="text-center">
                  <span>Media files</span>
                </th>
                <th className="text-center">
                  <span>Answer</span>
                </th>
                <th>
                  <span>Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {/* {data.data.map((quiz) => (
                <tr key={quiz.id} className="even:bg-slate-100 *:py-4 *:*:line-clamp-1">
                  <td>
                    <span>{quiz.id}</span>
                  </td>
                  <td>
                    <span className="capitalize">
                      {quiz.category.toLocaleLowerCase()}
                    </span>
                  </td>
                  <td>
                    <span>{Math.floor(quiz.duration / 60)} min</span>
                  </td>
                  <td>
                    <span>
                      {" "}
                      {quiz.expiratedAt === null
                        ? "No expirated"
                        : formatLocalDate(quiz.expiratedAt)}
                    </span>
                  </td>
                  <td>
                    <div className="!flex gap-3">
                      <FaEdit
                        onClick={() => {
                          navigate(
                            routers.settingQuizDetails.replace(
                              ":id",
                              quiz.id.toString()
                            )
                          );
                        }}
                        cursor="pointer"
                        size={18}
                        className="hover:text-blue-500 transition-all duration-200"
                      ></FaEdit>
                      <FaRegTrashCan
                        cursor={"pointer"}
                        size={18}
                        onClick={() => {
                          handleDeleteQuiz(quiz.id);
                        }}
                        className="hover:text-red-500 transition-all duration-200"
                      ></FaRegTrashCan>

                      <TbFileExport
                        cursor={"pointer"}
                        size={18}
                        onClick={() => {
                          handleExportQuiz(quiz.id);
                        }}
                        className="hover:text-primary transition-all duration-200"
                      ></TbFileExport>
                    </div>
                  </td>
                </tr>
              ))} */}
              <tr className="even:bg-slate-100 *:py-4 *:*:line-clamp-1">
                <td>
                  <span>1</span>
                </td>
                <td>
                  <span className="capitalize">dbbsssssssss</span>
                </td>
                <td>
                  <div className="grid place-items-center">
                    <FaEye
                      cursor="pointer"
                      size={18}
                      onClick={() => toggleModal(setIsFileModalOpen, true)}
                      className="hover:text-primary transition-all duration-200"
                    />
                  </div>
                </td>
                <td>
                  <div className="grid place-items-center">
                    <FaEye
                      data-modal-target="updateProductModal"
                      data-modal-toggle="updateProductModal"
                      cursor="pointer"
                      size={18}
                      className="hover:text-primary transition-all duration-200"
                    />
                  </div>
                </td>

                <td>
                  <div className="!flex gap-3">
                    <FaEdit
                      onClick={() => {
                        handleOpenEditQuestionModal();
                      }}
                      cursor="pointer"
                      size={18}
                      className="hover:text-blue-500 transition-all duration-200"
                    ></FaEdit>
                    <FaRegTrashCan
                      cursor={"pointer"}
                      size={18}
                      onClick={() => {
                        handleDelete(1);
                      }}
                      className="hover:text-red-500 transition-all duration-200"
                    ></FaRegTrashCan>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div className="grid place-items-center mt-8">
        <button type="button" className="btn-custom">
          Save change
        </button>
      </div>
      {/* modal */}
      <MediaManagerModal
        isOpen={isFileModalOpen}
        onClose={() => toggleModal(setIsFileModalOpen, false)}
        onSelect={handleSelectMedia}
      />

      {/* create question form */}
      <QuestionModal
        isOpen={isModalOpen}
        onClose={() => toggleModal(setIsModalOpen, false)}
        onSubmit={handleQuestionSubmit}
      />

      {/* question banking */}
      <QuestionSidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
      {/* {error ? (
        <NoDataModel />
      ) : (
        <div>
          <h2 className="text-primary text-lg">Manage detail quiz</h2>
        </div>
      )} */}
    </>
  );
}

export default SettingQuizDetails;
