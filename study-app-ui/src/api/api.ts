import axios, { AxiosRequestConfig } from "axios";
import Swal from "sweetalert2";
import routers from "../configs/routers.ts";
import { fetchEnd, fetchStart } from "../redux/appSlice.ts";
import { logOutSuccess, refreshTokenSuccess } from "../redux/authSlice.ts";
import { store } from "../redux/store.ts";
import { getNavigate } from "../utils/navigate.ts";

// Create Axios instance
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8222/api/v1",
  withCredentials: true,
});
axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8222/api/v1",
  withCredentials: true,
});

// Extend AxiosRequestConfig to include _retry
interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  _retry?: boolean;
}

// Function to set auth token
export const setAuthToken = (token: string | null) => {
  if (token) {
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common["Authorization"];
  }
};
// Axios interceptor for handling token refresh
api.interceptors.response.use(
  (response) => response, // Success handler, return the response as is
  async (error) => {
    const originalRequest = error.config as
      | CustomAxiosRequestConfig
      | undefined;

    // Ensure the originalRequest exists before processing it
    if (!originalRequest) {
      return Promise.reject(error);
    }

    // Check if token is expired (401) and not retried already
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // Mark the request as retried

      const state = store.getState();
      const userId = state.auth.user?.id;

      if (!userId) {
        // If no user ID, possibly logged out or session expired
        return Promise.reject(error);
      }

      try {
        store.dispatch(fetchStart());
        setAuthToken(null);
        store.dispatch(refreshTokenSuccess(""));
        // Call API to refresh token
        const response = await api.post(`/auth/users/${userId}/refresh-token`);
        const accessToken = response.data;

        // Update token in headers and retry the original request
        store.dispatch(refreshTokenSuccess(accessToken));
        setAuthToken(accessToken);
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }

        // Retry the original request with the new token
        return api(originalRequest);
      } catch (refreshError) {
        // Handle token refresh failure, e.g., logout and alert user
        store.dispatch(logOutSuccess());

        await Swal.fire({
          title: "Session expired!",
          text: "Your session has expired, please log in again.",
          icon: "warning",
          showConfirmButton: true,
          confirmButtonText: "Ok",
        }).then((result) => {
          if (result.isConfirmed) {
            const navigate = getNavigate();
            navigate(routers.login); // Redirect to login page after confirmation
          }
        });

        // Set a custom flag to indicate the error has been handled
        (refreshError as any).isHandled = true;

        return Promise.reject(refreshError);
      } finally {
        store.dispatch(fetchEnd());
      }
    }

    return Promise.reject(error); // Reject if not a 401 error or already retried
  }
);

api.interceptors.request.use(
  (config) => {
    // Lấy token từ localStorage (hoặc nơi bạn đã lưu)
    const state = store.getState();
    const token = state.auth.token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
