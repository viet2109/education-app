import { FC } from "react";

export interface Route {
  path: string;
  page: FC<any>;
  layout: FC<any>;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignUpRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
}

export interface UserInfo {
  id: string;
  name: string;
  email: string;
  phone: string;
}

export interface UserResponseLogin {
  accessToken: string;
  user: UserInfo;
}

export interface Category {
  title: string;
  imageUrl: string;
}

export interface Quiz {
  id: number; // ID của quiz
  title: string; // Tiêu đề của quiz
  category: string; // Danh mục của quiz, sử dụng kiểu dữ liệu Category đã được định nghĩa
  duration: number; // Thời gian của quiz (tính bằng giây)
  expiratedAt: string; // Thời điểm hết hạn của quiz
  listQuestion: QuestionResponseDto[]; // Danh sách các câu hỏi, sử dụng kiểu dữ liệu QuestionResponseDto
  updatedAt: string;
}

export interface QuizRequest {
  title: string; // Tiêu đề của quiz
  category: string; // Danh mục của quiz, sử dụng kiểu dữ liệu Category đã được định nghĩa
  duration?: number; // Thời gian của quiz (tính bằng giây)
  expiratedAt?: string; // Thời điểm hết hạn của quiz
}

export interface QuestionResponseDto {
  id?: number; // ID của câu hỏi
  content: string; // Nội dung câu hỏi
  listAnswer: AnswerResponseDto[]; // Danh sách các câu trả lời, sử dụng kiểu dữ liệu AnswerResponseDto
  examId: number; // ID của bài thi (hoặc quiz) mà câu hỏi thuộc về
  files: (File | Media)[]; // Danh sách URL của các file liên quan đến câu hỏi
  updatedAt: string;
}

export interface AnswerResponseDto {
  id?: number; // ID của câu trả lời
  content: string; // Nội dung câu trả lời
  files: (File | Media)[]; // Danh sách URL của các file liên quan đến câu trả lời
  isCorrect?: boolean;
}

export interface Page<T> {
  content: T[]; // Dữ liệu của trang hiện tại (một mảng đối tượng).
  number: number; // Số trang hiện tại (bắt đầu từ 0).
  size: number; // Số lượng phần tử trong mỗi trang.
  totalElements: number; // Tổng số phần tử trong toàn bộ dữ liệu.
  totalPages: number; // Tổng số trang.
  empty: boolean;
  numberOfElements: number;
  pageable: Pageable;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
}

export interface Pagination {
  page: number; // Số trang hiện tại
  pageSize: number; // Số lượng mục trên mỗi trang
  totalItems: number; // Tổng số mục
  totalPages: number; // Tổng số trang
}

export interface PaginatedResponse<T> {
  data: T[]; // Danh sách dữ liệu trên trang
  pagination: Pagination; // Thông tin phân trang
}

interface BasePaginationFilter {
  page?: number; // Trang hiện tại (mặc định là 0)
  size?: number; // Số lượng kết quả mỗi trang (mặc định là 10)
  sort?: string[]; // Mảng các trường và thứ tự sắp xếp (mặc định là ["id,asc"])
}

export interface QuizAnswer {
  userAnswers: {
    [questionId: number]: number[]; // Map từ Question ID (number) tới danh sách Answer ID (number[])
  };
  userId: string; // userId bắt buộc, không được để trống
  startedAt: string; // Thời gian bắt đầu (ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss)
  finishedAt: string; // Thời gian kết thúc (ISO 8601 format: yyyy-MM-dd'T'HH:mm:ss)
}

export interface ExamHistoryDetail {
  id: number;
  questionId: number;
  answerId: number;
  isCorrect: boolean;
}

export interface ExamHistory {
  id: number; // Long id (converted to number trong TypeScript)
  startedAt: string; // LocalDateTime, có thể sử dụng dạng ISO 8601 string (YYYY-MM-DDTHH:MM:SS)
  finishedAt: string; // LocalDateTime, dạng ISO 8601 string (YYYY-MM-DDTHH:MM:SS)
  score: number; // Double score (chuyển thành kiểu number trong TypeScript)
  userId: string; // String userId
  exam: Quiz; // Một đối tượng Quiz
  examHistoryDetail: ExamHistoryDetail[]; // Một mảng các đối tượng ExamHistoryDetail
}

export interface QuizAnswerResponse {
  score: number;
  quiz: QuestionResponseDto[];
}

export interface QuestionBankPaginationFilter extends BasePaginationFilter {
  category?: string[]; // Tìm theo danh mục (có thể bỏ qua)
  createdBy?: string; // Tìm theo người tạo (có thể bỏ qua)
  excludeExamIds?: number[]; // Tìm theo người tạo (có thể bỏ qua)
}

export interface QuizPaginationFilter extends BasePaginationFilter {
  title?: string; // Tìm theo tiêu đề (có thể bỏ qua)
  category?: string[]; // Tìm theo danh mục (có thể bỏ qua)
  createdBy?: string; // Tìm theo người tạo (có thể bỏ qua)
  minDuration?: number; // Tìm theo thời gian tối thiểu (có thể bỏ qua)
  maxDuration?: number; // Tìm theo thời gian tối đa (có thể bỏ qua)
  expiratedAtAfter?: string; // Tìm theo thời gian hết hạn sau (có thể bỏ qua)
  expiratedAtBefore?: string; // Tìm theo thời gian hết hạn trước (có thể bỏ qua)
  paged?: boolean
}
export interface Media {
  id: number; // Sử dụng number cho kiểu Long
  filename: string;
  fileUrl: string;
  fileType: string;
  sizeInBytes: number; // Sử dụng number cho kiểu long
  createdDate: string; // Sử dụng string cho LocalDateTime (ISO 8601 format)
  updatedDate: string; // Sử dụng string cho LocalDateTime (ISO 8601 format)
}
