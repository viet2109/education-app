import { LoginRequest, SignUpRequest, UserResponseLogin } from "../types";
import { store } from "../redux/store.ts";
import { fetchEnd, fetchStart } from "../redux/appSlice.ts";
import { loginSuccess, logOutSuccess } from "../redux/authSlice.ts";
import { api, setAuthToken } from "./api.ts";

export const login = async (
  loginRequest: LoginRequest
): Promise<UserResponseLogin> => {
  store.dispatch(fetchStart());
  try {
    const response = await api.post("/auth/login", loginRequest);
    const data: UserResponseLogin = response.data;
    store.dispatch(loginSuccess(data));
    setAuthToken(data.accessToken);
    return data;
  } catch (error: any) {
    

    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    store.dispatch(fetchEnd());
  }
};

export const register = async (signUpRequest: SignUpRequest) => {
  store.dispatch(fetchStart());
  try {
    await api.post("/auth/signup", signUpRequest);
  } catch (error: any) {
    

    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    store.dispatch(fetchEnd());
  }
};

export const logout = async (userId: string) => {
  store.dispatch(fetchStart());
  try {
    await api.post(`/auth/users/${userId}/logout`);
    store.dispatch(logOutSuccess());
  } catch (error: any) {
    

    return Promise.reject(error); // Trả lỗi về cho caller
  } finally {
    store.dispatch(fetchEnd());
  }
};
