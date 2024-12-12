import { dispatch, store } from "../redux/store.ts";
import { fetchEnd, fetchStart } from "../redux/appSlice.ts";
import { api } from "./api.ts";
import {
  PaginatedResponse,
  Pagination,
  QuestionBankPaginationFilter,
  QuestionResponseDto,
} from "../types/index.ts";

export const deleteQuestion = async (id: number): Promise<void> => {
  dispatch(fetchStart()); // Bắt đầu fetch
  try {
    await api.delete(`/questions/${id}`);
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const createQuestion = async (questionData: QuestionResponseDto) => {
  const formData = new FormData();

  // Thêm nội dung của câu hỏi
  formData.append("content", questionData.content);
  formData.append("examId", String(questionData.examId));

  // Thêm các file cho câu hỏi
  questionData.files.forEach((file, index) => {
    if (file instanceof File) {
      formData.append(`files[${index}]`, file);
    }
  });

  // Thêm danh sách câu trả lời
  questionData.listAnswer.forEach((answer, answerIndex) => {
    formData.append(`listAnswer[${answerIndex}].content`, answer.content);
    if (answer.isCorrect !== undefined) {
      formData.append(
        `listAnswer[${answerIndex}].isCorrect`,
        String(answer.isCorrect)
      );
    }

    // Thêm file cho từng câu trả lời
    answer.files.forEach((file, fileIndex) => {
      if (file instanceof File) {
        formData.append(`listAnswer[${answerIndex}].files[${fileIndex}]`, file);
      }
    });
  });

  // Gửi request với Axios
  try {
    const response = await api.post("/questions", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    console.log("Question created successfully:", response.data);
  } catch (error) {
    console.error("Error creating question:", error);
    throw error;
  }
};

export const createQuestionByQuestionBank = async (
  questionIds: Number[],
  examId: Number
) => {
  // Gửi request với Axios
  try {
    const response = await api.post("/questions/bank", { questionIds, examId });
    console.log("Question created successfully:", response.data);
    return response.data;
  } catch (error) {
    console.error("Error creating question:", error);
    throw error;
  }
};

export const getQuestionsBank = async (
  filters: QuestionBankPaginationFilter
): Promise<PaginatedResponse<QuestionResponseDto>> => {
  // store.dispatch(fetchStart());
  try {
    const response = await api.get("/questions/bank", {
      params: { ...filters },
      paramsSerializer: {
        indexes: null,
      },
    });
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // store.dispatch(fetchEnd());
  }
};
