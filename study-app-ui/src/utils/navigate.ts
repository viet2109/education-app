import { NavigateFunction } from 'react-router-dom';

let navigateFunction: NavigateFunction | null = null;

// Hàm để thiết lập navigate
export const setNavigate = (navigate: NavigateFunction) => {
  navigateFunction = navigate;
};

// Hàm để lấy navigate
export const getNavigate = (): NavigateFunction => {
  if (!navigateFunction) {
    throw new Error('Navigate function chưa được thiết lập!');
  }
  return navigateFunction;
};
