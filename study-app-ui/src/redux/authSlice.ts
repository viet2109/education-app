import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { UserInfo, UserResponseLogin } from "../types";

interface AuthState {
  user: UserInfo | null;
  token: string | null;
}

const initialState: AuthState = {
  user: null,
  token: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    loginSuccess: (state, action: PayloadAction<UserResponseLogin>) => {
      state.user = {
        ...action.payload.user,
      };
      state.token = action.payload.accessToken;
    },
    logOutSuccess: (state) => {
      state.user = null;
      state.token = null;
    },
    // updateUserSuccess: (state, action) => {
    //     state.user = {
    //         ...state.user,
    //         ...action.payload,
    //     };
    // },
    refreshTokenSuccess: (state, action: PayloadAction<string>) => {
      state.token = action.payload;
    },
  },
});

export const {
  loginSuccess,
  logOutSuccess,
  // updateUserSuccess,
  refreshTokenSuccess,
} = authSlice.actions;

export default authSlice.reducer;
