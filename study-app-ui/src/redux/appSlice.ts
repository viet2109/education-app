import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  loading: false,
};

const appSlice = createSlice({
  name: "app",
  initialState,
  reducers: {
    fetchStart: (state) => {
      state.loading = true;
    },
    fetchEnd: (state) => {
      state.loading = false;
    },
  },
});

export const { fetchStart, fetchEnd } = appSlice.actions;

export default appSlice.reducer;
