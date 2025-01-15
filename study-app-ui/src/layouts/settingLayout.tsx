import React from "react";
import { FaUserCircle } from "react-icons/fa";
import { MdOutlineHistory, MdQuiz } from "react-icons/md";
import { NavLink } from "react-router-dom";
import Footer from "../components/footer.tsx";
import Header from "../components/header.tsx";
import routers from "../configs/routers.ts";

interface Props {
  children: React.ReactElement;
}

function SettingLayout(props: Props) {
  const { children } = props;
  return (
    <>
      <Header />
      <div className={"mt-20 mb-10 px-default flex"}>
        {/* sidebar */}
        <nav className="sticky top-20 h-fit">
          <ul className="pt-5 flex flex-col group gap-6">
            <li>
              <NavLink className={({isActive}) => `flex items-center group-hover:gap-3 lg:gap-3 pr-5 hover:decoration-black underline transition-all duration-300 decoration-transparent ${isActive?"text-primary hover:decoration-primary":""}`} to={routers.settingProfile}>
                <FaUserCircle size={20}></FaUserCircle>
                <span className="line-clamp-1 max-w-28 w-0 group-hover:w-28 lg:w-28 transition-all duration-300">Thông tin chung</span>
              </NavLink>
            </li>
            <li>
              <NavLink className={({isActive}) => `flex items-center group-hover:gap-3 lg:gap-3 pr-5 hover:decoration-black underline decoration-transparent ${isActive?"text-primary hover:decoration-primary":""}`} to={routers.settingQuiz}>
                <MdQuiz size={20}></MdQuiz>
                <span className="line-clamp-1 max-w-28 w-0 group-hover:w-28 lg:w-28 transition-all duration-300">Quản lý bài thi</span>
              </NavLink>
            </li>
            <li>
              <NavLink className={({isActive}) => `flex items-center group-hover:gap-3 lg:gap-3 pr-5 hover:decoration-black underline decoration-transparent ${isActive?"text-primary hover:decoration-primary":""}`} to={routers.examHistories}>
                <MdOutlineHistory size={20}></MdOutlineHistory>
                <span className="line-clamp-1 max-w-28 w-0 group-hover:w-28 lg:w-28 transition-all duration-300">Lịch sử làm bài</span>
              </NavLink>
            </li>
          </ul>
        </nav>
        <div className="pl-5 pt-5 flex-1">{children}</div>
      </div>
      <Footer />
    </>
  );
}

export default SettingLayout;
