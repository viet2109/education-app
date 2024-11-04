// components/Modal.tsx
import React, { ReactNode, useEffect, useState } from "react";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
}

const Modal: React.FC<ModalProps> = ({ isOpen, onClose, children }) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsVisible(true);
    } else {
      setTimeout(() => setIsVisible(false), 300); // Delay để chạy xong hiệu ứng hide
    }
  }, [isOpen]);

  if (!isVisible) return null;

  return (
    <div className="fixed z-50 inset-0 bg-black bg-opacity-50 flex justify-center px-8 md:px-0 items-center">
      <div
        className={`bg-white p-6 rounded-lg shadow-lg w-full max-w-3xl relative ${
          isOpen ? "swal2-show" : "swal2-hide"
        }`}
      >
        <button
          onClick={onClose}
          className="absolute  rounded-lg top-3 text-2xl right-4 text-gray-400 hover:text-red-500"
        >
          ✕
        </button>
        <div className="mt-8">{children}</div>
      </div>
    </div>
  );
};

export default Modal;
