import React, { useEffect, useState } from "react";
import Select, { MultiValue } from "react-select"; // Import React Select
import { FaAngleDown, FaAngleUp } from "react-icons/fa6";
import { PaginatedResponse, QuestionResponseDto } from "../types";
import { useQuery } from "@tanstack/react-query";
import { DEFAULT_SLATE_TIME } from "../constant";
import {
  createQuestionByQuestionBank,
  getQuestionsBank,
} from "../api/question";
import Paginate from "./paginate";

const QuestionSidebar: React.FC<{
  isOpen: boolean;
  examId: number;
  toggleSidebar: () => void;
  handleCreate: () => void;
}> = ({ isOpen, toggleSidebar, examId, handleCreate }) => {
  const [selectedQuestion, setSelectedQuestion] =
    useState<QuestionResponseDto | null>(null);
  const [selectedQuestions, setSelectedQuestions] = useState<number[]>([]);
  const [randomCount, setRandomCount] = useState(5);
  const [page, setPage] = useState<number>(0);
  const [categoryFilter, setCategoryFilter] = useState<
    MultiValue<{ value: string; label: string }>
  >([]);
  const [createdByFilter, setCreatedByFilter] = useState<string>("");
  const [sortOption, setSortOption] = useState<{
    label: string;
    value: string;
  }>({ label: "ID Ascending", value: "id,asc" });
  const [isFilterVisible, setIsFilterVisible] = useState(true);
  const {
    data: questions,
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ["questions-bank", categoryFilter, createdByFilter, sortOption],
    queryFn: () => {
      const queryParameters = Object.fromEntries(
        Object.entries({
          category:
            categoryFilter.map((option) => option.value).length > 0
              ? categoryFilter.map((option) =>
                  encodeURIComponent(option.value.toLowerCase())
                )
              : undefined,
          createdBy: createdByFilter || undefined,
          page: page,
          excludeExamIds: [examId],
          sort: sortOption.value || undefined,
        }).filter(([_, value]) => value !== undefined)
      );
      return getQuestionsBank(queryParameters);
    },
    staleTime: DEFAULT_SLATE_TIME,
    retry: false,
    enabled: false,
  });

  useEffect(() => {
    // Refetch questions when filters change
    if (isOpen) refetch();
  }, [categoryFilter, createdByFilter, sortOption, page, refetch]);

  const allQuestionsSelected =
    selectedQuestions.length === questions?.data.length;

  const handleQuestionClick = (question: QuestionResponseDto) => {
    setSelectedQuestion((prev) => (prev?.id === question.id ? null : question));
  };

  const createQuestionByQBank = useQuery({
    queryKey: ["create-question-bank"],
    queryFn: () => {
      return createQuestionByQuestionBank(selectedQuestions, examId);
    },
    staleTime: DEFAULT_SLATE_TIME,
    retry: false,
    enabled: false, // Disabled by default; only refetches when filters change.
  });

  const createQuestionFromSelected = async () => {
    await createQuestionByQBank.refetch();

    if (createQuestionByQBank.isSuccess) {
      handleCreate();
    }
    setSelectedQuestions([]);
  };

  const toggleSelectQuestion = (questionId: number) => {
    setSelectedQuestions((prevSelected) =>
      prevSelected.includes(questionId)
        ? prevSelected.filter((id) => id !== questionId)
        : [...prevSelected, questionId]
    );
  };

  const toggleSelectAllQuestions = () => {
    setSelectedQuestions(
      allQuestionsSelected ? [] : questions?.data.map((q) => q.id!) || []
    );
  };

  const randomSelectQuestions = (count: number) => {
    if (!questions || questions.data.length === 0) return;
    const questionIds = questions.data.map((q) => q.id!).filter((id) => id);
    const randomIds = questionIds
      .sort(() => 0.5 - Math.random())
      .slice(0, Math.min(count, questionIds.length));
    setSelectedQuestions(randomIds);
  };

  const closeModal = () => {
    toggleSidebar();
    setSelectedQuestions([]);
  };

  const toggleBodyScroll = (lock: boolean) => {
    document.body.style.overflow = lock ? "hidden" : "auto";
  };

  useEffect(() => {
    toggleBodyScroll(isOpen);
    if (isOpen) {
      refetch();
    }
    return () => toggleBodyScroll(false);
  }, [isOpen]);

  const isSelectedQuestion = (id: number) => selectedQuestion?.id === id;

  const sortOptions = [
    { label: "ID Ascending", value: "id,asc" },
    { label: "ID Descending", value: "id,desc" },
  ];

  const categoryOptions = [
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

  return (
    <div className="relative z-50">
      <div
        className={`fixed z-20 top-0 right-0 w-2/3 max-w-lg h-full rounded-tl-3xl bg-white shadow-lg transition-transform duration-300 transform ${
          isOpen ? "translate-x-0" : "translate-x-full"
        } flex flex-col`}
      >
        <div className="flex justify-end mt-3 mr-4">
          <button
            onClick={closeModal}
            className="text-slate-400 text-2xl font-bold hover:text-red-500 transition-all duration-300"
            aria-label="Close Sidebar"
          >
            ✕
          </button>
        </div>

        <div className="p-8 pt-2 pb-4 border-b">
          <h2 className="text-xl font-semibold text-gray-700">Question Bank</h2>
        </div>

        {/* Filter Section */}
        <button
          onClick={() => setIsFilterVisible(!isFilterVisible)}
          className="btn-custom mt-4 w-fit ml-8"
        >
          {isFilterVisible ? "Hide Filters" : "Show Filters"}
        </button>
        <div
          className={`border-b flex flex-col items-center *:w-full ${
            isFilterVisible
              ? "opacity-100 h-32 p-4 px-8"
              : "opacity-0 h-0 overflow-hidden"
          } transition-all duration-300`}
        >
          <input
            type="text"
            placeholder="Filter by Creator"
            value={createdByFilter}
            onChange={(e) => setCreatedByFilter(e.target.value)}
            className="border-none hidden p-2 rounded w-full mb-4 outline-none focus:outline-primary transition-all duration-300 outline-slate-300"
          />
          <Select
            value={categoryFilter}
            onChange={(selected) => setCategoryFilter(selected || [])}
            options={categoryOptions}
            styles={{
              control: (baseStyles) => ({
                ...baseStyles,
                borderWidth: "2px",
                borderColor: "#94a3b8",
                cursor: "pointer",
                boxShadow: "none",
                paddingLeft: "6px",
                "&:focus-within": {
                  borderColor: "#27b489",
                  boxShadow: "0 0 0.2rem rgba(39, 180, 137, 1)",
                },
              }),
            }}
            isMulti
            placeholder="Select Category"
            className="mb-4"
          />
          <Select
            value={sortOption}
            onChange={(selected) =>
              setSortOption(selected as { label: string; value: string })
            }
            styles={{
              control: (baseStyles) => ({
                ...baseStyles,
                borderWidth: "2px",
                borderColor: "#94a3b8",
                boxShadow: "none",
                paddingLeft: "6px",
                cursor: "pointer",
                "&:focus-within": {
                  borderColor: "#27b489",
                  boxShadow: "0 0 0.2rem rgba(39, 180, 137, 1)",
                },
              }),
            }}
            options={sortOptions}
            placeholder="Sort By"
            className="mb-4"
          />
        </div>

        <div className="flex-grow p-8 pt-4 overflow-y-auto">
          {questions && questions.data.length > 0 ? (
            <>
              <div className="flex items-center mb-4">
                <input
                  type="checkbox"
                  id="select-all"
                  className="mr-4 cursor-pointer"
                  checked={allQuestionsSelected}
                  onChange={toggleSelectAllQuestions}
                />
                <label htmlFor="select-all" className="cursor-pointer">
                  Select All Questions
                </label>
              </div>

              <div className="flex items-center mb-4">
                <input
                  type="number"
                  value={randomCount}
                  onChange={(e) => setRandomCount(Number(e.target.value))}
                  min="1"
                  max={questions?.data.length || 1}
                  className="border rounded p-2 mr-2 w-20 outline-none focus:border-primary"
                  aria-label="Number of random questions"
                />
                <button
                  onClick={() => randomSelectQuestions(randomCount)}
                  className="bg-primary h-full text-white px-4 py-2 line-clamp-1 rounded hover:bg-opacity-80 transition"
                >
                  Random Select {randomCount} Questions
                </button>
              </div>
            </>
          ) : (
            <></>
          )}

          {questions && questions.data.length > 0 ? (
            <>
              <ul className="space-y-2">
                {questions.data.map((question) => (
                  <li key={question.id}>
                    <div className="flex items-center gap-2">
                      <input
                        type="checkbox"
                        id={`question-${question.id}`}
                        className="cursor-pointer"
                        checked={selectedQuestions.includes(question.id!)}
                        onChange={() =>
                          question.id && toggleSelectQuestion(question.id)
                        }
                      />

                      <button
                        onClick={() =>
                          question.id && handleQuestionClick(question)
                        }
                        className={`text-left w-full flex justify-between gap-2 items-start p-2 hover:bg-gray-100 rounded ${
                          isSelectedQuestion(question.id!) ? "bg-gray-100" : ""
                        }`}
                        aria-expanded={isSelectedQuestion(question.id!)}
                      >
                        <span className="font-bold text-gray-800 line-clamp-2">
                          {question.content}
                        </span>
                        {isSelectedQuestion(question.id!) ? (
                          <FaAngleUp
                            size={18}
                            className="min-w-[18px] aspect-square translate-y-1"
                          />
                        ) : (
                          <FaAngleDown
                            size={18}
                            className="min-w-[18px] aspect-square translate-y-1"
                          />
                        )}
                      </button>
                    </div>

                    <ol
                      className={`ml-8 flex flex-col gap-2 transition-all duration-500 ease-in-out overflow-hidden ${
                        isSelectedQuestion(question.id!)
                          ? "max-h-40"
                          : "max-h-0"
                      }`}
                    >
                      {isSelectedQuestion(question.id!) &&
                        question.listAnswer?.map((answer) => (
                          <li
                            key={answer.id}
                            className="list-[upper-alpha] list-inside list-item"
                          >
                            {answer.content}
                          </li>
                        ))}
                    </ol>
                  </li>
                ))}
              </ul>
              <Paginate
                onPageChange={(page) => {
                  setPage(page);
                }}
                itemsLength={questions.pagination.totalItems}
                initialPage={page}
                numberItemOnPage={questions.pagination.pageSize}
              />
            </>
          ) : (
            <p className="text-center text-gray-500 mt-4">
              No questions available.
            </p>
          )}
        </div>
        {/* Create Question Button */}
        {selectedQuestions.length > 0 && (
          <div className="p-4 px-8">
            <button
              onClick={createQuestionFromSelected}
              className="w-full bg-blue-600 text-white py-2  rounded-lg hover:bg-blue-700 transition"
            >
              Create Question from Selected
            </button>
          </div>
        )}
      </div>

      {isOpen && (
        <div
          onClick={closeModal}
          className="fixed inset-0 bg-gray-900 opacity-50 z-10"
          aria-hidden="true"
        ></div>
      )}
    </div>
  );
};

export default QuestionSidebar;
