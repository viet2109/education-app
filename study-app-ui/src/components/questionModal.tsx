import React, {
  useState,
  useEffect,
  ChangeEvent,
  FormEvent,
  useRef,
} from "react";
import Modal from "./modal";
import { Media } from "../types";

interface QuestionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (question: Question) => void;
  initialQuestion?: Question;
}

export interface Answer {
  content: string;
  files: (File | Media)[];
  isCorrect: boolean;
}

export interface Question {
  content: string;
  listAnswer: Answer[];
  files: (File | Media)[];
}

const QuestionModal: React.FC<QuestionModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialQuestion,
}) => {
  const [questionText, setQuestionText] = useState<string>("");
  const [answers, setAnswers] = useState<Answer[]>([]);
  const [questionFiles, setQuestionFiles] = useState<(File | Media)[]>([]);
  const [isVisible, setIsVisible] = useState(isOpen);
  const formRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen) {
      if (initialQuestion) {
        setQuestionText(initialQuestion.content);
        setAnswers(initialQuestion.listAnswer);
        setQuestionFiles(initialQuestion.files);
      } else {
        setQuestionText("");
        setAnswers([{ content: "", files: [], isCorrect: false }]);
        setQuestionFiles([]);
      }
      setIsVisible(true);
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
      setTimeout(() => setIsVisible(false), 250);
    }
  }, [isOpen, initialQuestion]);

  const handleQuestionChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setQuestionText(e.target.value);
  };

  const handleAnswerChange = (
    index: number,
    field: keyof Answer,
    value: string | boolean | (File | Media)[]
  ) => {
    const newAnswers = [...answers];
    newAnswers[index][field] = value as never;
    setAnswers(newAnswers);
  };

  const addAnswer = () => {
    setAnswers([...answers, { content: "", files: [], isCorrect: false }]);
  };

  const removeAnswer = (index: number) => {
    setAnswers(answers.filter((_, i) => i !== index));
  };

  const handleFileUpload = (
    e: ChangeEvent<HTMLInputElement>,
    index: number
  ) => {
    if (e.target.files) {
      const filesArray = Array.from(e.target.files);
      const newAnswers = [...answers];
      newAnswers[index].files = [...newAnswers[index].files, ...filesArray];
      setAnswers(newAnswers);
    }
  };

  const handleQuestionFileUpload = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const filesArray = Array.from(e.target.files);
      setQuestionFiles([...questionFiles, ...filesArray]);
    }
  };

  const removeQuestionFile = (index: number) => {
    setQuestionFiles(questionFiles.filter((_, i) => i !== index));
  };

  const removeAnswerFile = (answerIndex: number, fileIndex: number) => {
    const newAnswers = [...answers];
    newAnswers[answerIndex].files = newAnswers[answerIndex].files.filter(
      (_, i) => i !== fileIndex
    );
    setAnswers(newAnswers);
  };

  const handleFormSubmit = (e: FormEvent) => {
    e.preventDefault();
    const question: Question = {
      content: questionText,
      listAnswer: answers,
      files: questionFiles,
    };
    onSubmit(question);
    setQuestionText("");
    setAnswers([{ content: "", files: [], isCorrect: false }]);
    setQuestionFiles([]);
    onClose();
  };

  const renderFileName = (file: File | Media) => {
    return (file as File).name || (file as Media).filename;
  };

  if (!isVisible) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <div
        ref={formRef}
        className={`bg-white ${isOpen ? "swal2-show" : "swal2-hide"}`}
      >
        <h2 className="text-2xl font-semibold mb-4 text-gray-700">
          {initialQuestion ? "Update Question" : "Create a Question"}
        </h2>

        <form onSubmit={handleFormSubmit}>
          <div className="mb-4">
            <label
              className="block text-gray-600 font-medium mb-2"
              htmlFor="question"
            >
              Question
            </label>
            <div className="w-full flex mb-3">
              <textarea
                id="question"
                className="w-full p-3 m-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                placeholder="Enter your question here"
                value={questionText}
                onChange={handleQuestionChange}
              />
            </div>
            <input
              type="file"
              className="hidden"
              id="question-file-upload"
              multiple
              onChange={handleQuestionFileUpload}
            />
            <label
              htmlFor="question-file-upload"
              className="bg-blue-500 m-2 text-white px-4 py-2 rounded-lg cursor-pointer hover:bg-blue-600 transition mt-2"
            >
              Upload Question Files
            </label>
            {questionFiles.length > 0 && (
              <ul className="mt-2">
                {questionFiles.map((file, index) => (
                  <li
                    key={index}
                    className="flex justify-between text-gray-600"
                  >
                    {renderFileName(file)}
                    <button
                      type="button"
                      onClick={() => removeQuestionFile(index)}
                      className="text-red-500 hover:text-red-600 ml-2"
                    >
                      ✕
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="mb-4">
            <label className="block text-gray-600 font-medium mb-2">
              Answers
            </label>
            <div
              className="max-h-[300px] p-2 overflow-y-auto rounded-lg"
              style={{ height: "210px" }}
            >
              {answers.map((answer, index) => (
                <div
                  key={index}
                  className="flex items-start gap-3 flex-col mb-4"
                >
                  <div className="flex w-full gap-1">
                    <textarea
                      placeholder={`Answer ${index + 1}`}
                      className="w-full flex-1 p-3 border rounded-lg outline-none focus:ring-2 focus:ring-blue-400"
                      value={answer.content}
                      onChange={(e) =>
                        handleAnswerChange(index, "content", e.target.value)
                      }
                    />
                    <div className="flex items-center ml-3">
                      <input
                        type="checkbox"
                        checked={answer.isCorrect}
                        onChange={(e) =>
                          handleAnswerChange(
                            index,
                            "isCorrect",
                            e.target.checked
                          )
                        }
                        className="mr-2"
                      />
                      <span className="text-gray-600">Correct</span>
                    </div>
                    <button
                      type="button"
                      onClick={() => removeAnswer(index)}
                      className="ml-3 text-red-500 hover:text-red-600 text-xl mr-2"
                    >
                      ✕
                    </button>
                  </div>
                  <input
                    type="file"
                    className="hidden"
                    id={`file-upload-${index}`}
                    multiple
                    onChange={(e) => handleFileUpload(e, index)}
                  />
                  <label
                    htmlFor={`file-upload-${index}`}
                    className="bg-blue-500 text-white px-4 py-2 rounded-lg cursor-pointer hover:bg-blue-600 transition"
                  >
                    Upload Files
                  </label>
                  {answer.files.length > 0 && (
                    <ul className="mt-2">
                      {answer.files.map((file, fileIndex) => (
                        <li
                          key={fileIndex}
                          className="flex justify-between text-gray-600"
                        >
                          {renderFileName(file)}
                          <button
                            type="button"
                            onClick={() => removeAnswerFile(index, fileIndex)}
                            className="text-red-500 hover:text-red-600 ml-2"
                          >
                            ✕
                          </button>
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              ))}
              <button
                type="button"
                onClick={addAnswer}
                className="w-full bg-green-500 text-white py-2 rounded-lg mt-2 hover:bg-green-600 transition"
              >
                Add Answer
              </button>
            </div>
          </div>

          <div className="flex justify-end gap-2 mt-4">
            <button
              type="button"
              onClick={onClose}
              className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition"
            >
              Submit
            </button>
          </div>
        </form>
      </div>
    </Modal>
  );
};

export default QuestionModal;
