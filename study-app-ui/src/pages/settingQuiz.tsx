import { useQuery } from "@tanstack/react-query";
import { useEffect, useMemo, useState } from "react";
import { FaEdit } from "react-icons/fa";
import { FaPlus, FaRegTrashCan } from "react-icons/fa6";
import { TbFileExport } from "react-icons/tb";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import * as Yup from "yup";
import {
  createQuiz,
  deleteQuiz,
  exportQuiz,
  fetchQuizzes,
  importQuiz,
} from "../api/quiz";
import Modal from "../components/modal";
import NoDataModel from "../components/noDataModel";
import Pagianate from "../components/paginate";
import routers from "../configs/routers";
import { DEFAULT_SLATE_TIME } from "../constant";
import { formatLocalDate } from "../helper/formatLocalDate";
import { RootState } from "../redux/store";
import { QuizPaginationFilter, QuizRequest } from "../types";
import { FastField, Form, Formik } from "formik";
import InputField from "../components/inputField";
import Select from "react-select";

const quizSchema = Yup.object().shape({
  title: Yup.string().required("The title is mandatory"),
  category: Yup.string(),
  duration: Yup.number().min(60, "The duration must be at least 1 minute"),
  expiratedAt: Yup.date().min(
    new Date(),
    "The expiration date must be in the future"
  ),
});

function SettingQuiz() {
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);

  const queryParams = useMemo(
    () => new URLSearchParams(location.search),
    [location.search]
  );

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

  const paginationFilter: QuizPaginationFilter = useMemo(
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
      title: queryParams.has("title")
        ? queryParams.get("title") || undefined
        : undefined, // Chỉ trả về undefined nếu null
      category: queryParams.has("category")
        ? queryParams
            .getAll("category")
            .map((ct) => encodeURIComponent(ct.toLowerCase()))
        : undefined,
      createdBy: user?.id || undefined, // Chỉ trả về undefined nếu null
      minDuration: queryParams.has("minDuration")
        ? Number(queryParams.get("minDuration"))
        : undefined,
      maxDuration: queryParams.has("maxDuration")
        ? Number(queryParams.get("maxDuration"))
        : undefined,
      expiratedAtAfter: queryParams.has("expiratedAtAfter")
        ? queryParams.get("expiratedAtAfter") || undefined
        : undefined,
      expiratedAtBefore: queryParams.has("expiratedAtBefore")
        ? queryParams.get("expiratedAtBefore") || undefined
        : undefined,
    }),
    [queryParams]
  );

  const { data, refetch } = useQuery({
    queryKey: ["quizzes"],
    queryFn: () => fetchQuizzes(paginationFilter),
    staleTime: DEFAULT_SLATE_TIME,
    retry: false,
    enabled: false,
  });

  const handleDeleteQuiz = (id: number) => {
    Swal.fire({
      title: `Delete quiz`,
      text: `Do you want to delete quiz ${id}`,
      icon: "question",
      showCancelButton: true,
      cancelButtonColor: "#ef4444",
    }).then(async (value) => {
      if (value.isConfirmed) {
        Swal.fire({
          title: "Deleting quiz...",
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading();
          },
        });
        try {
          await deleteQuiz(id);
          refetch();
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
    });
  };

  const iQuiz = async (file: File) => {
    try {
      const quiz = await importQuiz(file);
      refetch();
      return quiz;
    } catch (error: any) {
      throw new Error(error.response);
    }
  };

  useEffect(() => {
    refetch();
  }, [refetch]);

  const handleCreateQuiz = () => {
    Swal.fire({
      title: "Select Quiz Creation Method",
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
                                <div class="w-full text-lg text-left font-semibold">Import from File</div>
                                <div class="w-full text-gray-500 text-sm text-left">Upload a file to automatically generate a quiz from the prepared content.<br><font color="red"><i>Please note that this feature currently supports Word and Excel files only.*</i></font>
</br></div>
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
      confirmButtonColor: "#27b489",
      cancelButtonColor: "#ef4444",
    }).then((result) => {
      if (result.isConfirmed) {
        if (result.value === "manually") {
          setQuizModalOpen(true);
        } else if (result.value === "import") {
          Swal.fire({
            title: "Select file",
            input: "file",
            inputAttributes: {
              accept: ".doc,.docx,.xls,.xlsx",
              "aria-label": "Import your file here",
            },
            cancelButtonColor: "#ef4444",
            showCancelButton: true,
            confirmButtonColor: "#27b489",
            preConfirm(inputValue) {
              if (!inputValue)
                Swal.showValidationMessage("Please select a file to continue");
            },
            icon: "info",
          }).then(async (value) => {
            const file = value.value;

            if (file) {
              Swal.fire({
                title: "Creating new quiz...",
                allowOutsideClick: false,
                didOpen: () => {
                  Swal.showLoading();
                },
              });
              try {
                // Gọi hàm iQuiz và chờ đợi kết quả
                await iQuiz(file);

                Swal.fire({
                  icon: "success",
                  title: "Quiz created successfully",
                  showConfirmButton: true,
                  confirmButtonText: "OK",
                });
              } catch (error) {
                Swal.fire({
                  icon: "error",
                  title: "Create failed",
                  text: "An error occurred while creating the quiz. Please try again.",
                  confirmButtonColor: "#ef4444",
                });
              }
            }
          });
        }
      }
    });
  };

  function handleExportQuiz(id: number) {
    Swal.fire({
      title: `Export quiz ${id}`,
      text: "Please choose type of file to export",
      showCancelButton: true,
      cancelButtonColor: "#ef4444",
      confirmButtonText: "Export",
      html: `<ul class="flex justify-center gap-3">
              <li>
                  <label for="t-1" class="inline-flex items-center justify-between w-fit p-5 text-gray-900 bg-white border-2 border-gray-300 rounded-lg cursor-pointer has-[:checked]:border-primary has-[:checked]:border-2 has-[:checked]:text-primary hover:text-gray-900 hover:bg-gray-100">                           
                  <input type="radio" id="t-1" name="type" value="word" class="!hidden peer" required />
                      
                  <div class="flex flex-col items-center gap-2">
                          <svg class='w-7' xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path fill="#74C0FC" d="M48 448L48 64c0-8.8 7.2-16 16-16l160 0 0 80c0 17.7 14.3 32 32 32l80 0 0 288c0 8.8-7.2 16-16 16L64 464c-8.8 0-16-7.2-16-16zM64 0C28.7 0 0 28.7 0 64L0 448c0 35.3 28.7 64 64 64l256 0c35.3 0 64-28.7 64-64l0-293.5c0-17-6.7-33.3-18.7-45.3L274.7 18.7C262.7 6.7 246.5 0 229.5 0L64 0zm55 241.1c-3.8-12.7-17.2-19.9-29.9-16.1s-19.9 17.2-16.1 29.9l48 160c3 10.2 12.4 17.1 23 17.1s19.9-7 23-17.1l25-83.4 25 83.4c3 10.2 12.4 17.1 23 17.1s19.9-7 23-17.1l48-160c3.8-12.7-3.4-26.1-16.1-29.9s-26.1 3.4-29.9 16.1l-25 83.4-25-83.4c-3-10.2-12.4-17.1-23-17.1s-19.9 7-23 17.1l-25 83.4-25-83.4z"/></svg>
                          <p class='font-semibold'>Word type (.docx)</p>
                    </div>
                  </label>
              </li>
                            <li>
                  <label for="t-2" class="inline-flex items-center justify-between w-fit p-5 text-gray-900 bg-white border-2 border-gray-300 rounded-lg cursor-pointer has-[:checked]:border-primary has-[:checked]:border-2 has-[:checked]:text-primary hover:text-gray-900 hover:bg-gray-100">                           
                  <input type="radio" id="t-2" name="type" value="excel" class="!hidden peer" required />
                      
                  <div class="flex flex-col items-center gap-2">
                          <svg class='w-7' xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512"><path fill="#63E6BE" d="M48 448L48 64c0-8.8 7.2-16 16-16l160 0 0 80c0 17.7 14.3 32 32 32l80 0 0 288c0 8.8-7.2 16-16 16L64 464c-8.8 0-16-7.2-16-16zM64 0C28.7 0 0 28.7 0 64L0 448c0 35.3 28.7 64 64 64l256 0c35.3 0 64-28.7 64-64l0-293.5c0-17-6.7-33.3-18.7-45.3L274.7 18.7C262.7 6.7 246.5 0 229.5 0L64 0zm90.9 233.3c-8.1-10.5-23.2-12.3-33.7-4.2s-12.3 23.2-4.2 33.7L161.6 320l-44.5 57.3c-8.1 10.5-6.3 25.5 4.2 33.7s25.5 6.3 33.7-4.2L192 359.1l37.1 47.6c8.1 10.5 23.2 12.3 33.7 4.2s12.3-23.2 4.2-33.7L222.4 320l44.5-57.3c8.1-10.5 6.3-25.5-4.2-33.7s-25.5-6.3-33.7 4.2L192 280.9l-37.1-47.6z"/></svg>
                          <p class='font-semibold'>Excel type (.xlsx)</p>
                    </div>
                  </label>
              </li>
            </ul>`,
      icon: "info",
      preConfirm: () => {
        const selectedValue = document.querySelector<HTMLInputElement>(
          'input[name="type"]:checked'
        )?.value;
        if (!selectedValue) {
          Swal.showValidationMessage("Please select an option to continue");
          return false;
        }
        return selectedValue;
      },
    }).then(async (value) => {
      if (value.isConfirmed) {
        const type = value.value;
        Swal.fire({
          title: "Exporting quiz...",
          allowOutsideClick: false,
          didOpen: () => {
            Swal.showLoading();
          },
        });
        try {
          await exportQuiz(id, type);
          Swal.fire({
            icon: "success",
            title: "Quiz exported successfully",
            showConfirmButton: true,
            confirmButtonText: "OK",
          });
        } catch (error) {
          Swal.fire({
            icon: "error",
            title: "Export failed",
            text: "An error occurred while exporting the quiz. Please try again.",
            confirmButtonColor: "#ef4444",
          });
        }
      }
    });
  }

  const handleSubmit = async (values: QuizRequest) => {
    // Handle submit form logic here (e.g., call API or update state)
    try {
      const examId = await createQuiz(values);
      navigate(routers.settingQuizDetails.replace(":id", examId.id.toString()));
    } catch (error) {
      console.error(error);
    }
  };

  const [quizModalOpen, setQuizModalOpen] = useState(false);
  return (
    <>
      <Modal
        isOpen={quizModalOpen}
        onClose={() => {
          setQuizModalOpen(false);
        }}
        children={
          <>
            <Formik
              initialValues={{
                title: "",
                category: categories[0].value,
                duration: 60,
                expiratedAt: "",
              }}
              validationSchema={quizSchema}
              onSubmit={handleSubmit}
            >
              {({ setFieldValue }) => (
                <Form>
                  <div className="mb-4">
                    <label htmlFor="title" className="block font-semibold mb-1">
                      Title
                    </label>
                    <FastField
                      name="title"
                      placeholder="Enter quiz title"
                      component={InputField}
                    />
                  </div>

                  <div className="mb-4">
                    <label
                      htmlFor="category"
                      className="block font-semibold mb-1"
                    >
                      Category
                    </label>
                    <Select
                      name="category"
                      options={categories}
                      styles={{
                        control: (baseStyles) => ({
                          ...baseStyles,
                          borderWidth: "2px",
                          borderRadius: "8px",
                          borderColor: "#e5e7eb",
                          boxShadow: "none",
                          paddingTop: "6px",
                          cursor: "pointer",
                          paddingBottom: "6px",
                          "&:hover": {},
                          "&:focus-within": {
                            borderColor: "#27b489",
                          },
                        }),
                      }}
                      onChange={(selectedOption) =>
                        setFieldValue("category", selectedOption?.value)
                      }
                      defaultValue={categories[0]}
                    />
                  </div>

                  <div className="mb-4">
                    <label
                      htmlFor="duration"
                      className="block font-semibold mb-1"
                    >
                      Duration (minutes)
                    </label>
                    <FastField
                      name="duration"
                      type="number"
                      placeholder="Enter duration"
                      component={InputField}
                    />
                  </div>

                  <div className="mb-4">
                    <label
                      htmlFor="expiratedAt"
                      className="block font-semibold mb-1"
                    >
                      Expiration Date
                    </label>
                    <FastField
                      name="expiratedAt"
                      type="datetime-local"
                      component={InputField}
                    />
                  </div>
                  <div className="flex justify-center">
                    <button
                      type="submit"
                      className="w-full max-w-32 btn-custom mt-4 bg-primary text-white rounded-lg"
                    >
                      Create Quiz
                    </button>
                  </div>
                </Form>
              )}
            </Formik>
          </>
        }
      ></Modal>
      <div>
        <button
          onClick={handleCreateQuiz}
          className="btn-custom flex gap-2 items-center"
        >
          <FaPlus></FaPlus>
          <span>New Quiz</span>
        </button>
      </div>
      {data && data?.data?.length > 0 ? (
        <>
          <table className="w-full text-left">
            <thead className="border-b-2">
              <tr className="*:py-4 *:*:line-clamp-1">
                <th className="first:pl-4 last:pr-4">
                  <span>Id</span>
                </th>
                <th className="first:pl-4 last:pr-4">
                  <span>Category</span>
                </th>
                <th className="first:pl-4 last:pr-4">
                  <span>Duration</span>
                </th>
                <th className="first:pl-4 last:pr-4">
                  <span>Expirated at</span>
                </th>
                <th className="first:pl-4 last:pr-4">
                  <span>Actions</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {data.data.map((quiz) => (
                <tr
                  key={quiz.id}
                  className="even:bg-slate-100 *:py-4 *:*:line-clamp-1"
                >
                  <td className="first:pl-4 last:pr-4">
                    <span>{quiz.id}</span>
                  </td>
                  <td className="first:pl-4 last:pr-4">
                    <span className="capitalize">
                      {quiz.category.toLocaleLowerCase()}
                    </span>
                  </td>
                  <td className="first:pl-4 last:pr-4">
                    <span>{Math.floor(quiz.duration / 60)} min</span>
                  </td>
                  <td className="first:pl-4 last:pr-4">
                    <span>
                      {" "}
                      {quiz.expiratedAt === null
                        ? "No expirated"
                        : formatLocalDate(quiz.expiratedAt)}
                    </span>
                  </td>
                  <td className="first:pl-4 last:pr-4">
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
              ))}
            </tbody>
          </table>
          <Pagianate
            onPageChange={() => {}}
            itemsLength={data.pagination.totalItems}
            numberItemOnPage={10}
            initialPage={0}
          />
        </>
      ) : (
        <NoDataModel title="You haven't create any quiz"></NoDataModel>
      )}
    </>
  );
}

export default SettingQuiz;
