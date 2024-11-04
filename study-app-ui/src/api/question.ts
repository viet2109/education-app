import { dispatch } from "../redux/store.ts";
import { fetchEnd, fetchStart } from "../redux/appSlice.ts";
import { api } from "./api.ts";


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

