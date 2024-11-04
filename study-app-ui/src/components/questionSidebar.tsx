import React, { useEffect, useState } from "react";
import { FaAngleDown, FaAngleUp } from "react-icons/fa6";
import { QuestionResponseDto } from "../types";

// Component QuestionSidebar
const QuestionSidebar: React.FC<{
  questions?: QuestionResponseDto[];
  isOpen: boolean;
  toggleSidebar: () => void;
}> = ({
  questions = [
    // Giá trị mặc định cho prop questions
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
        {
          id: 4,
          content: "A server-side logic.",
          files: [],
          isCorrect: false,
        },
      ],
      examId: 101,
      files: [],
    },
  ],
  isOpen,
  toggleSidebar,
}) => {
  const [selectedQuestion, setSelectedQuestion] =
    useState<QuestionResponseDto | null>(null);
  const [selectedQuestions, setSelectedQuestions] = useState<number[]>([]); // Lưu ID các câu hỏi được chọn

  const handleQuestionClick = (question: QuestionResponseDto) => {
    setSelectedQuestion((prev) => (prev?.id === question.id ? null : question));
  };

  const createQuestionFromSelected = () => {
    console.log("Created Question:", selectedQuestions);
    setSelectedQuestions([]); // Reset selected questions
  };

  const toggleSelectQuestion = (questionId: number) => {
    setSelectedQuestions(
      (prevSelected) =>
        prevSelected.includes(questionId)
          ? prevSelected.filter((id) => id !== questionId) // Bỏ chọn nếu đã chọn
          : [...prevSelected, questionId] // Chọn câu hỏi mới
    );
  };

  const toggleSelectAllQuestions = () => {
    setSelectedQuestions((prevSelected) =>
      prevSelected.length === questions.length
        ? []
        : questions.map((q) => q.id!)
    );
  };

  const closeModal = () => {
    toggleSidebar();
    setSelectedQuestions([]);
  };

  // Effect to control body scroll
  useEffect(() => {
    document.body.style.overflow = isOpen ? "hidden" : "auto"; // Ngăn chặn cuộn khi sidebar mở
  }, [isOpen]);

  return (
    <div className="relative z-50">
      {/* Sidebar */}
      <div
        className={`fixed z-20 top-0 right-0 w-2/3 h-full rounded-tl-3xl bg-white shadow-lg transition-transform duration-300 transform ${
          isOpen ? "translate-x-0" : "translate-x-full"
        } flex flex-col`}
      >
        {/* Button to toggle sidebar */}
        <div className="flex justify-end mt-3 mr-4">
          <button
            onClick={closeModal}
            className="text-slate-400 text-2xl font-bold hover:text-red-500 transition-all duration-300"
          >
            ✕
          </button>
        </div>
        <div className="p-8 pt-2 pb-4 border-b">
          <h2 className="text-xl font-semibold text-gray-700">Question Bank</h2>
        </div>
        <div className="flex-grow p-8 pt-4 overflow-y-auto">
          {/* Nút chọn tất cả câu hỏi */}
          <div className="flex items-center mb-4">
            <input
              type="checkbox"
              id="select-all"
              className="mr-4 cursor-pointer"
              checked={selectedQuestions.length === questions.length}
              onChange={toggleSelectAllQuestions}
            />
            <label htmlFor="select-all" className="cursor-pointer">
              Select All Questions
            </label>
          </div>
          {questions.length > 0 ? (
            <ul className="space-y-2">
              {questions.map((question) => (
                <li key={question.id}>
                  <div className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      id={`question-${question.id}`}
                      className="cursor-pointer"
                      checked={selectedQuestions.includes(question.id!)}
                      onChange={() => toggleSelectQuestion(question.id!)}
                    />
                    <button
                      onClick={() => handleQuestionClick(question)}
                      className="text-left w-full flex justify-between gap-2 items-center p-2 hover:bg-gray-100 rounded"
                    >
                      <span className="font-bold text-gray-800 line-clamp-2">
                        {question.content}
                      </span>
                      {selectedQuestion?.id === question.id ? (
                        <FaAngleUp size={18} />
                      ) : (
                        <FaAngleDown size={18} />
                      )}
                    </button>
                  </div>
                  {/* Hiển thị danh sách câu trả lời nếu câu hỏi được chọn */}
                  <ul
                    className={`ml-8 flex flex-col gap-2 transition-all duration-500 ease-in-out overflow-hidden ${
                      selectedQuestion?.id === question.id
                        ? "max-h-40"
                        : "max-h-0"
                    }`}
                  >
                    {selectedQuestion?.id === question.id &&
                      question.listAnswer.map((answer) => (
                        <li key={answer.id} className="flex items-center">
                          <input
                            type="checkbox"
                            id={`answer-${answer.id}`}
                            className="mr-4 rounded border-gray-300 focus:!shadow-none"
                            checked={answer.isCorrect} // Checkbox được đánh dấu dựa trên isCorrect
                            readOnly
                          />
                          <label
                            htmlFor={`answer-${answer.id}`}
                            className="text-gray-700 line-clamp-2"
                          >
                            {answer.content}
                          </label>
                        </li>
                      ))}
                  </ul>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500">No questions available.</p>
          )}
        </div>
        {/* Nút tạo câu hỏi từ các câu hỏi đã chọn */}
        {selectedQuestions.length > 0 && (
          <div className="p-4 px-8">
            <button
              onClick={createQuestionFromSelected}
              className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
            >
              Create Question from Selected
            </button>
          </div>
        )}
      </div>

      {/* Overlay */}
      {isOpen && (
        <div
          onClick={closeModal}
          className="fixed inset-0 bg-black opacity-50 z-10"
        ></div>
      )}
    </div>
  );
};

export default QuestionSidebar;
