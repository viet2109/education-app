import React, { useState } from "react";

interface ImageWithSkeletonProps {
  src: string;
  alt: string;
  skeletonClass?: string;
  imgClass?: string;
  className?: string;
}

const ImageWithSkeleton: React.FC<ImageWithSkeletonProps> = ({
  src,
  alt,
  className,
  skeletonClass,
  imgClass = "h-48 w-full object-cover",
}) => {
  const [isLoaded, setIsLoaded] = useState(false);

  return (
    <div
      className={`relative flex justify-center items-center ${className}`}
    >
      {!isLoaded && (
        <div
          className={`h-48 w-full rounded-xl bg-gray-300 animate-pulse ${skeletonClass}`}
        ></div>
      )}
      <img
        src={src}
        alt={alt}
        className={`${imgClass} ${!isLoaded ? "hidden" : ""}`}
        onLoad={() => setIsLoaded(true)}
      />
    </div>
  );
};

export default ImageWithSkeleton;
