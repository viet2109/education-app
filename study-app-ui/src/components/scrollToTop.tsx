// src/components/ScrollToTop.tsx
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const ScrollToTop: React.FC = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo({
      top: 0,
      left: 0,
      behavior: 'smooth', // Optional: for smooth scrolling
    });
  }, [pathname]);

  return null; // This component doesn't render anything
};

export default ScrollToTop;
