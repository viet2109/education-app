import Tippy from "@tippyjs/react";
import { useEffect, useRef, useState } from "react";
import { FaUserCircle } from "react-icons/fa";
import { ImSearch } from "react-icons/im";
import { IoCloseCircle, IoLogOut, IoSettings } from "react-icons/io5";
import { TbLoader2 } from "react-icons/tb";
import { useSelector } from "react-redux";
import { Link, NavLink, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "tippy.js/animations/perspective.css";
import { logout } from "../api/auth.ts";
import { images } from "../assets/images";
import routers from "../configs/routers.ts";
import { formatLocalDate } from "../helper/formatLocalDate.ts";
import useDebounce from "../hooks/useDebounce.tsx";
import { RootState } from "../redux/store.ts";
import { Category, Quiz } from "../types/index.ts";
import Menu from "./menu.tsx";
import { useQuery } from "@tanstack/react-query";
import { fetchQuiz, fetchQuizzes, getAllCategories } from "../api/quiz.ts";
import { DEFAULT_SLATE_TIME } from "../constant/index.ts";
import ImageWithSkeleton from "./imageWithSkeleton.tsx";

interface NavLink {
  title: string;
  router: string;
}

function Header() {
  const menu = useRef<HTMLButtonElement>(null);
  const handleCloseMenu = () => menu.current?.click();

  const [searchTerm, setSearchTerm] = useState<string>("");
  const debouncedSearchTerm = useDebounce<string>(searchTerm, 500);
  const [isSearching, setIsSearching] = useState<boolean>(false);
  const [isVisible, setIsVisible] = useState<boolean>(false);
  const { data, refetch } = useQuery({
    queryKey: ["search-exams"],
    queryFn: () => fetchQuizzes({ title: searchTerm, size: 5 }),
    enabled: false,
  });
  const { data: categoryList = [] } = useQuery<Category[], Error>({
    queryKey: ["categories"],
    queryFn: getAllCategories,
    staleTime: DEFAULT_SLATE_TIME,
  });
  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);
  const [navLinks] = useState<NavLink[]>([
    { title: "Trang chủ", router: routers.home },
    { title: "Môn học", router: routers.subject },
    { title: "Các bài thi", router: routers.exams },
  ]);

  const handleLogOut = () => {
    Swal.fire({
      title: "Đăng xuất",
      text: "Bạn có muốn đăng xuất không?",
      icon: "question",
      showCancelButton: true,
      cancelButtonColor: "#d33",
      showConfirmButton: true,
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          user && (await logout(user.id));
          navigate(routers.home);
        } catch (error) {
          console.error(error);
        }
      }
    });
  };

  useEffect(() => {
    const fetchSearchResults = async () => {
      if (!debouncedSearchTerm) {
        setIsSearching(false);
        setIsVisible(false);
        return;
      }

      try {
        setIsSearching(true);
        setIsVisible(true);
        await refetch();
      } catch (error) {
        console.error("Lỗi khi tìm kiếm kết quả", error);
      } finally {
        setIsSearching(false);
      }
    };

    fetchSearchResults();
  }, [debouncedSearchTerm, refetch]);

  const handleClearSearch = () => {
    setSearchTerm("");
    setIsVisible(false);
  };

  const handleInputFocus = () => setIsVisible(true);

  const handleClickOutside = () => setIsVisible(false);
  return (
    <header
      className={
        "shadow px-default py-default fixed bg-white top-0 left-0 right-0 h-20 z-50"
      }
    >
      <div
        className={
          "flex items-center justify-between max-w-default mx-auto h-full gap-4"
        }
      >
        <div className={"flex items-center justify-between md:gap-4"}>
          <Tippy
            placement="bottom-start"
            trigger={"click"}
            arrow={false}
            offset={[0, 20]}
            animation="perspective"
            reference={menu}
            interactive
            hideOnClick="toggle"
            onClickOutside={handleCloseMenu}
            content={
              <ul
                className={"shadow-custom bg-white rounded-lg overflow-hidden"}
              >
                {navLinks.map((navLink, index) => (
                  <li onClick={handleCloseMenu} key={index}>
                    <Link
                      className={
                        "px-4 py-2 block hover:bg-slate-300 hover:text-white "
                      }
                      to={navLink.router}
                    >
                      {navLink.title}
                    </Link>
                  </li>
                ))}
              </ul>
            }
          >
            <Menu ref={menu} />
          </Tippy>

          <Link to={routers.home}>
            <ImageWithSkeleton
              imgClass={"w-28 hidden md:block"}
              src={images.logo}
              skeletonClass="!w-28 !h-12 hidden md:block"
              alt="Logo"
            />
          </Link>
        </div>
        <div className="flex gap-4 items-center">
          <ul className={"hidden lg:flex"}>
            {navLinks.map((navLink, index) => (
              <li key={index}>
                <NavLink
                  className={({ isActive }) =>
                    `px-4 py-2 relative block hover:underline ${
                      isActive &&
                      "before:block before:top-0 before:left-1/2 before:-translate-x-1/2 before:animate-fade before:absolute before:h-1 before:bg-primary before:w-1/2 before:rounded-xl"
                    }`
                  }
                  to={navLink.router}
                >
                  <span className="line-clamp-1">{navLink.title}</span>
                </NavLink>
              </li>
            ))}
          </ul>
          {/* Ô tìm kiếm */}
          <Tippy
            placement="bottom-start"
            animation="perspective"
            interactive
            onClickOutside={handleClickOutside}
            visible={isVisible}
            content={
              <ul className="shadow-custom w-fit bg-white rounded-lg overflow-hidden">
                {isSearching ? (
                  <></>
                ) : data?.data.length === 0 ? (
                  <li className="p-4 min-w-72 gap-2 flex flex-col items-center text-center italic text-gray-400">
                    <ImageWithSkeleton
                      src={images.noData}
                      skeletonClass="h-36"
                      imgClass="w-24 grayscale"
                      alt="nodata"
                    />
                    <span>Không tìm thấy bài thi</span>
                  </li>
                ) : (
                  data?.data.map((result) => (
                    <li
                      key={result.id}
                      onClick={handleClickOutside}
                      className="px-4 py-3 hover:bg-slate-200 cursor-pointer group"
                    >
                      <Link
                        to={routers.instructionExam.replace(
                          ":id",
                          result.id.toString()
                        )}
                        className="flex gap-3"
                      >
                        <div className="w-fit">
                          <ImageWithSkeleton
                            src={
                              categoryList.find(
                                (ct) =>
                                  ct.title
                                    .trim()
                                    .replace(/\s+/g, "_")
                                    .toUpperCase() === result.category
                              )?.imageUrl || ""
                            }
                            alt="category"
                            skeletonClass="!w-16 !h-10"
                            imgClass="w-16 rounded-md"
                          />
                        </div>
                        <div>
                          <h2 className="group-hover:text-primary transition-all">
                            {result.title}
                          </h2>
                          <p className="text-gray-300 italic text-sm group-hover:text-primary transition-all">
                            Cập nhật: {formatLocalDate(result.updatedAt)}
                          </p>
                        </div>
                      </Link>
                    </li>
                  ))
                )}
              </ul>
            }
          >
            <div className="relative">
              <input
                type="text"
                value={searchTerm}
                spellCheck={false}
                onFocus={(e) => {
                  if (e.target.value.trim()) handleInputFocus();
                }}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="focus:outline-primary border transition-all  px-10 py-2 rounded-full outline-gray-300"
                placeholder="Tìm kiếm bài thi ..."
              />
              <ImSearch className="absolute  top-1/2 -translate-y-1/2 left-4 text-gray-300" />
              <div className="absolute text-xl top-1/2  -translate-y-1/2 right-4 text-gray-300">
                {isSearching ? (
                  <TbLoader2 className="animate-spin aspect-square" />
                ) : searchTerm ? (
                  <IoCloseCircle
                    className="cursor-pointer"
                    onClick={handleClearSearch}
                  />
                ) : null}
              </div>
            </div>
          </Tippy>
        </div>

        {user ? (
          <div className={"flex items-center gap-x-3"}>
            <Tippy
              maxWidth={""}
              onMount={(instance) => {
                instance.popper.style.margin = "0 auto";
              }}
              offset={[0, 15]}
              placement="bottom-start"
              arrow={false}
              animation="perspective"
              interactive
              content={
                <ul
                  className={
                    "shadow-custom bg-white rounded-lg overflow-hidden"
                  }
                >
                  <li onClick={handleLogOut}>
                    <div
                      className={
                        "px-4 cursor-pointer py-2 flex items-center gap-x-3 hover:bg-slate-300 hover:text-white transition-all duration-300"
                      }
                    >
                      <IoLogOut size={18} />
                      <span>Đăng xuất</span>
                    </div>
                  </li>

                  <li>
                    <Link
                      className={
                        "px-4 py-2 flex items-center gap-x-3 hover:text-white hover:bg-slate-300 transition-all duration-300"
                      }
                      to={routers.settingProfile}
                    >
                      <IoSettings size={18} />
                      <span>Cài đặt</span>
                    </Link>
                  </li>
                </ul>
              }
            >
              <div>
                <FaUserCircle size={24} cursor={"pointer"} />
              </div>
            </Tippy>
            <span className={"font-semibold line-clamp-1"}>{user.name}</span>
          </div>
        ) : (
          <div className={"flex gap-x-3"}>
            <Link className={"btn-custom"} to={routers.login}>
              <span className="line-clamp-1">Đăng nhập</span>
            </Link>
            <Link className={"btn-custom"} to={routers.signUp}>
              <span className="line-clamp-1">Đăng Ký</span>
            </Link>
          </div>
        )}
      </div>
    </header>
  );
}

export default Header;
