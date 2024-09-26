import {images} from "../assets/images";
import {Link, NavLink} from "react-router-dom";
import routers from "../configs/routers.ts";
import Menu from "./menu.tsx";
import Tippy from "@tippyjs/react";
import "tippy.js/animations/perspective.css";
import {useRef, useState} from "react";

interface NavLink {
    title: string,
    router: string
}

function Header() {
    const menu = useRef<HTMLButtonElement>(null);
    const handleCloseMenu = () => {
        menu.current && menu.current.click();
    }
    const [navLinks] = useState<NavLink[]>([{title: "Subject", router: routers.subject}, {
        title: "Home",
        router: routers.home
    }])
    return (
        <header className={"shadow px-default py-default fixed bg-white top-0 left-0 right-0 h-20 z-50"}>
            <div className={"flex items-center justify-between max-w-default mx-auto"}>
                <div className={"flex items-center justify-between gap-6"}>

                    <Tippy
                        maxWidth={""}
                        onMount={(instance) => {
                            instance.popper.style.margin = "0 auto";

                        }}
                        placement="bottom-start"
                        trigger={"click"}
                        arrow={false}
                        offset={[0, 30]}
                        animation="perspective"
                        reference={menu}

                        interactive

                        hideOnClick="toggle"
                        onClickOutside={handleCloseMenu}
                        content={
                            <ul className={"shadow-custom bg-white rounded-lg overflow-hidden"}>
                                {navLinks.map((navLink, index) => (
                                    <li onClick={handleCloseMenu} key={index}>
                                        <Link
                                            className={"px-4 py-2 block hover:bg-slate-300 transition-all duration-default"}
                                            to={navLink.router}>
                                            {navLink.title}
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        }
                    >
                        <Menu ref={menu}/>
                    </Tippy>


                    <Link to={routers.home}>
                        <img className={"w-28"} src={images.logo} alt="Logo"/>
                    </Link>
                </div>
                <ul className={"hidden lg:flex"}>
                    {navLinks.map((navLink, index) => (
                        <li key={index}>
                            <NavLink
                                className={({isActive}) => `px-4 py-2 relative block hover:underline transition-all duration-default ${isActive && 'before:block before:top-0 before:left-1/2 before:-translate-x-1/2 before:animate-fade before:absolute before:h-1 before:bg-primary before:w-1/2 before:rounded-xl'}`}
                                to={navLink.router}>
                                {navLink.title}
                            </NavLink>
                        </li>
                    ))}
                </ul>
                <div className={"flex gap-x-3"}>
                    <button className={"btn-custom"}>Login</button>
                    <button className={"btn-custom"}>Sign Up</button>
                </div>
            </div>
        </header>
    );
}

export default Header;