import { images } from "../assets/images";
import { Link, NavLink, useNavigate } from "react-router-dom";
import routers from "../configs/routers.ts";
import Menu from "./menu.tsx";
import Tippy from "@tippyjs/react";
import "tippy.js/animations/perspective.css";
import { useRef, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../redux/store.ts";
import { FaUserCircle } from "react-icons/fa";
import { IoLogOut, IoSettings } from "react-icons/io5";
import { logout } from "../api/auth.ts";
import Swal from "sweetalert2";

interface NavLink {
  title: string;
  router: string;
}

function Header() {
  const menu = useRef<HTMLButtonElement>(null);
  const handleCloseMenu = () => {
    menu.current && menu.current.click();
  };

  const navigate = useNavigate();
  const user = useSelector((state: RootState) => state.auth.user);
  const [navLinks] = useState<NavLink[]>([
    {
      title: "Home",
      router: routers.home,
    },
    { title: "Subject", router: routers.subject },
    { title: "Exams", router: routers.exams },
  ]);

  function handleLogOut() {
    Swal.fire({
      title: "Log Out",
      text: "Do you want to log out?",
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
  }

  return (
    <header
      className={
        "shadow px-default py-default fixed bg-white top-0 left-0 right-0 h-20 z-50"
      }
    >
      <div
        className={
          "flex items-center justify-between max-w-default mx-auto h-full"
        }
      >
        <div className={"flex items-center justify-between gap-6"}>
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
            <img className={"w-28"} src={images.logo} alt="Logo" />
          </Link>
        </div>
        <ul className={"hidden lg:flex"}>
          {navLinks.map((navLink, index) => (
            <li key={index}>
              <NavLink
                className={({ isActive }) =>
                  `px-4 py-2 relative block hover:underline  ${
                    isActive &&
                    "before:block before:top-0 before:left-1/2 before:-translate-x-1/2 before:animate-fade before:absolute before:h-1 before:bg-primary before:w-1/2 before:rounded-xl"
                  }`
                }
                to={navLink.router}
              >
                {navLink.title}
              </NavLink>
            </li>
          ))}
        </ul>
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
                      <span>Log out</span>
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
                      <span>Setting</span>
                    </Link>
                  </li>
                </ul>
              }
            >
              <div>
                <FaUserCircle size={24} cursor={"pointer"} />
              </div>
            </Tippy>
            <span className={"font-semibold"}>{user.name}</span>
          </div>
        ) : (
          <div className={"flex gap-x-3"}>
            <Link className={"btn-custom"} to={routers.login}>
              Login
            </Link>
            <Link className={"btn-custom"} to={routers.signUp}>
              Sign Up
            </Link>
          </div>
        )}
      </div>
    </header>
  );
}

export default Header;
