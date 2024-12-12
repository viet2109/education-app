import {
  Category,
  PaginatedResponse,
  Quiz,
  QuizPaginationFilter,
  QuizRequest,
} from "../types";
import { dispatch, store } from "../redux/store.ts";
import { fetchEnd, fetchStart } from "../redux/appSlice.ts";
import { api } from "./api.ts";

export const getAllCategories = async (): Promise<Category[]> => {
  // store.dispatch(fetchStart());
  try {
    const response = await api.get(`/quizzes/categories`);
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // store.dispatch(fetchEnd());
  }
};

export const fetchQuizzes = async (
  filters: QuizPaginationFilter = {}
): Promise<PaginatedResponse<Quiz>> => {
  // dispatch(fetchStart()); // Bắt đầu fetch
  try {
    const response = await api.get<PaginatedResponse<Quiz>>("/quizzes", {
      params: {
        ...filters,
      },
      paramsSerializer: {
        indexes: null,
      },
    });

    // Trả về dữ liệu phân trang
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const fetchManageQuizzes = async (
  filters: QuizPaginationFilter = {}
): Promise<PaginatedResponse<Quiz>> => {
  // dispatch(fetchStart()); // Bắt đầu fetch
  try {
    const response = await api.get<PaginatedResponse<Quiz>>(
      "/quizzes/settings",
      {
        params: {
          ...filters,
        },
        paramsSerializer: {
          indexes: null,
        },
      }
    );

    // Trả về dữ liệu phân trang
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const fetchQuiz = async (quizId: number): Promise<Quiz> => {
  // dispatch(fetchStart()); // Bắt đầu fetch
  try {
    const response = await api.get<Quiz>(`/quizzes/${quizId}`);
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const fetchManageQuiz = async (quizId: number): Promise<Quiz> => {
  // dispatch(fetchStart()); // Bắt đầu fetch
  try {
    const response = await api.get<Quiz>(`/quizzes/settings/${quizId}`);
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const importQuiz = async (file: File): Promise<Quiz> => {
  dispatch(fetchStart()); // Bắt đầu fetch
  try {
    const form = new FormData();
    form.append("file", file);
    const response = await api.post<Quiz>(`/quizzes/import`, form, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const deleteQuiz = async (id: number): Promise<void> => {
  dispatch(fetchStart()); // Bắt đầu fetch
  try {
    await api.delete(`/quizzes/${id}`);
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const exportQuiz = async (id: number, type?: string): Promise<void> => {
  dispatch(fetchStart()); // Bắt đầu fetch
  const typeFile: { [key: string]: string } = {
    word: "docx",
    excel: "xlsx",
  };
  try {
    const response = await api.get(`/quizzes/${id}/export`, {
      params: {
        fileType: type,
      },
      responseType: "blob",
    });

    // Extract filename from headers if available
    const contentDisposition = response.headers["content-disposition"];

    const fileName = contentDisposition
      ? contentDisposition.split("filename=")[1].replace(/"/g, "")
      : `export.${typeFile[type as keyof typeof typeFile] || typeFile["word"]}`;

    // Create a Blob from the response data and a URL to download it
    const blob = new Blob([response.data], {
      type: response.headers["content-type"],
    });
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    link.download = fileName;
    link.click();

    // Cleanup the URL object after download
    window.URL.revokeObjectURL(link.href);
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    dispatch(fetchEnd()); // Kết thúc fetch
  }
};

export const createQuiz = async (quiz: QuizRequest): Promise<Quiz> => {
  dispatch(fetchStart()); // Bắt đầu fetch

  try {
    const response = await api.post(`/quizzes`, quiz);
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    dispatch(fetchEnd()); // Kết thúc fetch
  }
};
