import {NavLink} from "react-router-dom";
import {images} from "../assets/images";
import routers from "../configs/routers.ts";

function Subject() {
    const categoryList = [
        {name: "wassce/gce", image: images.category},
    ];

    return (
        <div className="py-10 max-w-default mx-auto">
            <h1 className="mb-5 text-primary capitalize text-xl font-medium">Select subject</h1>
            <ul className=" grid sm:grid-cols-2 gap-6 lg:grid-cols-4">
                {categoryList.map((category) => {
                    return (
                        <>
                            <li key={category.name} className="">
                                <NavLink
                                    className="flex h-full flex-col items-center gap-2 shadow-custom p-4 rounded-lg"
                                    to={routers.instructionExam}
                                    state={category.name}
                                >
                                    <img
                                        className="w-28 h-24 object-contain object-top"
                                        src={category.image}
                                        alt="logo_category"
                                    />
                                    <span className=" font-[500] w-full break-words">
                    {category.name}
                  </span>
                                </NavLink>
                            </li>

                            <li key={category.name} className="">
                                <NavLink
                                    className="flex h-full flex-col items-center gap-2 shadow-custom p-4 rounded-lg"
                                    to={routers.instructionExam}
                                    state={category.name}
                                >
                                    <img
                                        className="w-28 h-24 object-contain object-top"
                                        src={category.image}
                                        alt="logo_category"
                                    />
                                    <span className="font-[500] w-full break-words">
                    {category.name}
                  </span>
                                </NavLink>
                            </li>

                            <li key={category.name} className="">
                                <NavLink
                                    className="flex h-full flex-col items-center gap-2 shadow-custom p-4 rounded-lg"
                                    to={routers.instructionExam}
                                    state={category.name}
                                >
                                    <img
                                        className="w-28 h-24 object-contain object-top"
                                        src={category.image}
                                        alt="logo_category"
                                    />
                                    <span className=" font-[500] w-full break-words">
                    {category.name}
                  </span>
                                </NavLink>
                            </li>
                        </>
                    );
                })}
            </ul>
        </div>
    );
}

export default Subject;
