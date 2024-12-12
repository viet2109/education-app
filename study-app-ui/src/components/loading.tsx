import { useEffect } from "react";

function Loading() {
  useEffect(() => {
    // Ngăn cuộn khi component được mount
    document.body.style.overflow = "hidden";

    // Cho phép cuộn lại khi component unmount
    return () => {
      document.body.style.overflow = "";
    };
  }, []);

  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-80 flex justify-center items-center">
      <div className="loader flex items-center justify-center">
        <div className="dot bg-white m-2.5 animate-dot1"></div>
        <div className="dot bg-white m-2.5 animate-dot2"></div>
        <div className="dot bg-white m-2.5 animate-dot3"></div>
        <div className="dot bg-white m-2.5 animate-dot4"></div>
        <div className="dot bg-white m-2.5 animate-dot5"></div>
      </div>
    </div>
  );
}

export default Loading;
