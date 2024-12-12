
const QuizItemSkeleton = () => {
  return (
    <div className="animate-pulse">
      <li className="before:block before:w-6 before:h-2 before:bg-gray-300 before:rounded-full before:absolute before:-top-3 before:left-0 relative">
        {/* Skeleton cho ID */}
        <div className="w-20 h-4 bg-gray-300 rounded mb-2"></div>

        {/* Skeleton cho Title */}
        <div className="w-3/4 h-6 bg-gray-300 rounded mb-2"></div>

        {/* Skeleton cho Last Updated */}
        <div className="w-1/2 h-4 bg-gray-300 rounded"></div>
      </li>
    </div>
  );
};

export default QuizItemSkeleton;
