import { ExamHistory, Page } from "../types";
import { api } from "./api";
export interface ExamHistoryFilter {
  userId?: string;
  page?: number;
  size?: number;
  sort?: string[];
}
export const fetchExamHistoryById = async (
  id: number
): Promise<ExamHistory> => {
  // store.dispatch(fetchStart());
  try {
    const response = await api.get(`/exam-histories/${id}`);
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // store.dispatch(fetchEnd());
  }
};

export const fetchExamHistories = async (
  filter: ExamHistoryFilter
): Promise<Page<ExamHistory>> => {
  // store.dispatch(fetchStart());
  try {
    const response = await api.get(`/exam-histories`, {
      params: {
        ...filter,
      },
      paramsSerializer: { indexes: null },
    });
    return response.data;
  } catch (error: any) {
    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    // store.dispatch(fetchEnd());
  }
};
