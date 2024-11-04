import React, {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { fetchQuizzes } from "../api/quiz";
import { PaginatedResponse, Quiz, QuizPaginationFilter } from "../types";
import NoDataModal from "../components/noDataModel";
import { FaFilter, FaSort } from "react-icons/fa6";
import Tippy from "@tippyjs/react";
import Select, { MultiValue } from "react-select";
import { formatLocalDate } from "../helper/formatLocalDate";
import routers from "../configs/routers";
import Pagianate from "../components/paginate";

const categoriesOptions = [
  { value: "MATHEMATICS", label: "Mathematics" },
  { value: "LITERATURE", label: "Literature" },
  { value: "NATURAL_SCIENCES", label: "Natural Sciences" },
  { value: "SOCIAL_SCIENCES", label: "Social Sciences" },
  { value: "FOREIGN_LANGUAGES", label: "Foreign Languages" },
  { value: "INFORMATION_TECHNOLOGY", label: "Information Technology" },
  { value: "ART", label: "Art" },
  { value: "ECONOMICS", label: "Economics" },
  { value: "HEALTH", label: "Health" },
  { value: "SPORTS", label: "Sports" },
];

const Exams: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const filterBtn = useRef<HTMLButtonElement>(null);
  const sortBtn = useRef<HTMLButtonElement>(null);

  const queryParams = useMemo(
    () => new URLSearchParams(location.search),
    [location.search]
  );

  const [quizzes, setQuizzes] = useState<Quiz[]>([]);

  const [selectedCategories, setSelectedCategories] = useState<
    MultiValue<{ value: string; label: string }>
  >(() => {
    const categories = queryParams.getAll("category");
    return categories.map((cat) => {
      const categoryOption = categoriesOptions.find(
        (option) => option.value === cat
      );
      return categoryOption ? categoryOption : { value: cat, label: cat };
    });
  });

  const [sortOption, setSortOption] = useState<string[]>(
    queryParams.has("sort") ? queryParams.getAll("sort") : ["id,asc"]
  );

  const [totalItems, setTotalItems] = useState<number>(0);

  const paginationFilter: QuizPaginationFilter = useMemo(
    () => ({
      page: queryParams.has("page")
        ? Number(queryParams.get("page")) - 1
        : undefined,
      size: queryParams.has("size")
        ? Number(queryParams.get("size"))
        : undefined,
      sort: queryParams.has("sort")
        ? queryParams.getAll("sort").map((s) => s)
        : undefined,
      title: queryParams.has("title")
        ? queryParams.get("title") || undefined
        : undefined, // Chỉ trả về undefined nếu null
      category: queryParams.has("category")
        ? queryParams
            .getAll("category")
            .map((ct) => encodeURIComponent(ct.toLowerCase()))
        : undefined,
      createdBy: queryParams.has("createdBy")
        ? queryParams.get("createdBy") || undefined
        : undefined, // Chỉ trả về undefined nếu null
      minDuration: queryParams.has("minDuration")
        ? Number(queryParams.get("minDuration"))
        : undefined,
      maxDuration: queryParams.has("maxDuration")
        ? Number(queryParams.get("maxDuration"))
        : undefined,
      expiratedAtAfter: queryParams.has("expiratedAtAfter")
        ? queryParams.get("expiratedAtAfter") || undefined
        : undefined,
      expiratedAtBefore: queryParams.has("expiratedAtBefore")
        ? queryParams.get("expiratedAtBefore") || undefined
        : undefined,
    }),
    [queryParams]
  );

  const getAllQuiz = useCallback(async () => {
    try {
      const data: PaginatedResponse<Quiz> = await fetchQuizzes(
        paginationFilter
      );
      setQuizzes(data.data);
      setTotalItems(data.pagination.totalItems);
    } catch (err) {
      console.error(err);
    }
  }, [paginationFilter]);

  useEffect(() => {
    getAllQuiz();
    window.scrollTo({
      top: 0,
      left: 0,
      behavior: "smooth",
    });
  }, [getAllQuiz]);

  const handleCloseTippy = (ref: React.RefObject<HTMLButtonElement>) => {
    setTimeout(() => {
      if (ref.current) ref.current.click();
    }, 100);
  };

  const handleFilterChange = (
    selectedOptions: MultiValue<{ value: string; label: string }>
  ) => {
    setSelectedCategories(selectedOptions);
    const params = new URLSearchParams(location.search);
    handleCloseTippy(filterBtn);

    if (selectedOptions && selectedOptions.length > 0) {
      params.delete("category")
      selectedOptions.forEach((opt) => {
        params.append(
          "category",
          encodeURIComponent(opt.value.toLocaleLowerCase())
        );
      });
    } else {
      params.delete("category");
    }
    navigate({ search: params.toString() });
  };

  const handleClearAllFilters = () => {
    // Clear selected categories and reset the filter states
    setSelectedCategories([]);

    // Remove all query parameters from the URL
    const params = new URLSearchParams();

    // Điều hướng lại mà không có bộ lọc nào
    navigate({ search: params.toString() });
    handleCloseTippy(filterBtn);
  };

  const handleSortChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { id } = event.currentTarget;
    let newSort = "";

    switch (id) {
      case "id_asc":
        newSort = "id,asc";
        break;
      case "id_desc":
        newSort = "id,desc";
        break;
      case "createdAt_asc":
        newSort = "createdAt,asc";
        break;
      case "createdAt_desc":
        newSort = "createdAt,desc";
        break;
      default:
        newSort = "id,asc";
    }

    setSortOption(() => [newSort]);
    const params = new URLSearchParams(location.search);
    params.set("sort", newSort);
    params.delete("page");
    navigate({ search: params.toString() });
    handleCloseTippy(sortBtn);
  };

  const handlePageChange = (newPage: number) => {
    const params = new URLSearchParams(location.search);
    params.set("page", (newPage + 1).toString());
    navigate({ search: params.toString() });
  };

  return (
    <div className="py-8">
      <div className="flex gap-x-3">
        <Tippy
          animation="perspective"
          placement="top-end"
          interactive
          hideOnClick
          trigger="click"
          onMount={() => {
            if (filterBtn.current)
              filterBtn.current.classList.add("!bg-primary", "!text-white");
          }}
          onHidden={() => {
            if (filterBtn.current)
              filterBtn.current.classList.remove("!bg-primary", "!text-white");
          }}
          content={
            <ul className="bg-white shadow-custom rounded-lg p-4 min-w-96 cursor-pointer">
              <li className="flex flex-col gap-y-2">
                <span className="font-semibold">Categories</span>
                <Select
                  styles={{
                    control: (baseStyles) => ({
                      ...baseStyles,
                      cursor: "pointer",
                    }),
                  }}
                  isMulti
                  name="filters"
                  options={categoriesOptions}
                  value={selectedCategories}
                  onChange={handleFilterChange}
                />
              </li>

              <li className="mt-4 flex gap-x-6">
                <button
                  className="btn-custom !bg-red-500 text-white !bg-opacity-50 flex-1 hover:!bg-opacity-100 text-center font-semibold"
                  onClick={handleClearAllFilters}
                >
                  Clear All
                </button>
              </li>
            </ul>
          }
        >
          <button
            ref={filterBtn}
            className="btn-custom flex items-center gap-x-2"
          >
            <FaFilter />
            Filter
          </button>
        </Tippy>

        <Tippy
          trigger="click"
          animation="perspective"
          placement="top-end"
          hideOnClick
          interactive
          onMount={() => {
            if (sortBtn.current)
              sortBtn.current.classList.add("!bg-primary", "!text-white");
          }}
          onHidden={() => {
            if (sortBtn.current)
              sortBtn.current.classList.remove("!bg-primary", "!text-white");
          }}
          content={
            <ul className="bg-white shadow-custom rounded-lg overflow-hidden px-4 *:flex *:items-center *:gap-x-3 *:cursor-pointer *:my-4">
              <li className="*:flex-1">
                <div className="pr-4 flex items-center border-r-2">
                  <label htmlFor="id_asc" className="cursor-pointer flex-1">
                    Id (A - Z)
                  </label>
                  <input
                    type="radio"
                    name="sort"
                    id="id_asc"
                    value="id,asc" // Cần có value
                    className="cursor-pointer"
                    checked={sortOption.includes("id,asc")} // So sánh với giá trị
                    onChange={handleSortChange} // Trình xử lý sự kiện onChange
                  />
                </div>

                <div className="pl-4 flex items-center">
                  <label htmlFor="id_desc" className="cursor-pointer flex-1">
                    Id (Z-A)
                  </label>
                  <input
                    type="radio"
                    name="sort"
                    id="id_desc"
                    value="id,desc" // Cần có value
                    className="cursor-pointer"
                    checked={sortOption.includes("id,desc")} // So sánh với giá trị
                    onChange={handleSortChange} // Trình xử lý sự kiện onChange
                  />
                </div>
              </li>

              <li className="border-t-2 *:flex-1">
                <div className="flex items-center gap-x-3 cursor-pointer my-4 border-r-2 pr-4">
                  <label htmlFor="createdAt_asc" className="cursor-pointer">
                    Created Date (Oldest - Newest)
                  </label>
                  <input
                    type="radio"
                    name="sort"
                    id="createdAt_asc"
                    value="createdAt,asc" // Cần có value
                    className="cursor-pointer"
                    checked={sortOption.includes("createdAt,asc")} // So sánh với giá trị
                    onChange={handleSortChange} // Trình xử lý sự kiện onChange
                  />
                </div>

                <div className="flex items-center gap-x-3 cursor-pointer my-4 pl-4">
                  <label htmlFor="createdAt_desc" className="cursor-pointer">
                    Created Date (Newest - Oldest)
                  </label>
                  <input
                    type="radio"
                    name="sort"
                    id="createdAt_desc"
                    value="createdAt,desc" // Cần có value
                    className="cursor-pointer"
                    checked={sortOption.includes("createdAt,desc")} // So sánh với giá trị
                    onChange={handleSortChange} // Trình xử lý sự kiện onChange
                  />
                </div>
              </li>
            </ul>
          }
        >
          <button
            ref={sortBtn}
            className="btn-custom flex items-center gap-x-2"
          >
            <FaSort />
            Sort
          </button>
        </Tippy>
      </div>
      {quizzes.length === 0 ? (
        <NoDataModal title="No exams found" />
      ) : (
        <>
          <ul className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 *:shadow-custom *:p-6 *:rounded-lg my-4 mt-8">
            {quizzes.map((quiz) => (
              <Link
                to={routers.instructionExam.replace(":id", quiz.id.toString())}
                key={quiz.id}
                className="hover:bg-primary hover:text-white  group "
              >
                <li className="before:block before:w-6 before:h-2 before:bg-primary before:rounded-full before:absolute before:-top-3 before:left-0 relative group-hover:before:bg-white">
                  <span>Id: {quiz.id}</span>
                  <p className="quiz-title line-clamp-1">Title: {quiz.title}</p>
                  <span className="text-sm italic text-gray-400 group-hover:text-white">
                    Last updated at: {formatLocalDate(quiz.updatedAt)}
                  </span>
                </li>
              </Link>
            ))}
          </ul>

          <Pagianate
            initialPage={paginationFilter.page || 0}
            onPageChange={function (numberPage: number): void {
              handlePageChange(numberPage);
            }}
            itemsLength={totalItems}
            numberItemOnPage={
              queryParams.has("size") ? Number(queryParams.get("size")) : 12
            }
          />
        </>
      )}
    </div>
  );
};

export default Exams;
