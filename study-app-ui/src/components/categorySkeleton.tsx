const CategorySkeleton = () => {
  return (
    <div className="animate-pulse">
      <div className="flex h-full flex-col items-center justify-center gap-2 shadow-custom p-4 rounded-lg bg-gray-200">
        {/* Skeleton cho hình ảnh */}
        <div className="w-20 aspect-square bg-gray-300 rounded-full"></div>

        {/* Skeleton cho tiêu đề */}
        <div className="w-3/4 h-6 bg-gray-300 rounded"></div>
      </div>
    </div>
  );
};

export default CategorySkeleton;
