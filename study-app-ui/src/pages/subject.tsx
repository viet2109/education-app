import { NavLink } from "react-router-dom";
import { getAllCategories } from "../api/quiz.ts";
import NoDataModel from "../components/noDataModel.tsx";
import routers from "../configs/routers.ts";
import { useQuery } from "@tanstack/react-query";
import { Category } from "../types/index.ts";
import { DEFAULT_SLATE_TIME } from "../constant/index.ts";
import CategorySkeleton from "../components/categorySkeleton.tsx";

function Subject() {
  const { data: categoryList = [], isLoading } = useQuery<Category[], Error>({
    queryKey: ["categories"],
    queryFn: getAllCategories,
    staleTime: DEFAULT_SLATE_TIME,
  });

  return (
    <div className="py-10 max-w-default mx-auto">
      {categoryList.length === 0 ? (
        <NoDataModel className="!p-0" title="No category found" />
      ) : (
        <>
          <h1 className="mb-5 text-primary capitalize text-xl font-medium">
            Select subject
          </h1>
          {isLoading ? (
            <ul className="grid sm:grid-cols-2 gap-6 lg:grid-cols-4">
              {Array.from({ length: 4 }).map((_, index) => (
                <CategorySkeleton key={index} />
              ))}
            </ul>
          ) : (
            <ul className="grid sm:grid-cols-2 gap-6 lg:grid-cols-4">
              {categoryList.map((category) => (
                <li key={category.title}>
                  <NavLink
                    className="flex h-full flex-col items-center justify-center gap-2 shadow-custom p-4 rounded-lg hover:bg-primary hover:text-white "
                    to={{
                      pathname: routers.exams,
                      search: `?category=${category.title
                        .trim()
                        .replace(/\s+/g, "_")
                        .toLowerCase()}`,
                    }}
                  >
                    <img
                      className="w-20 aspect-square object-cover object-center rounded-full"
                      src={category.imageUrl}
                      alt="logo_category"
                      loading={"lazy"}
                    />
                    <span className="font-[500] text-center w-full break-words">
                      {category.title}
                    </span>
                  </NavLink>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </div>
  );
}

export default Subject;
