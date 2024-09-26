import React, {useState, forwardRef} from 'react';

interface MenuProps {
    onClick?: (event: React.MouseEvent<HTMLButtonElement>) => void;
}

// Sử dụng forwardRef để truyền ref vào button
const Menu = forwardRef<HTMLButtonElement, MenuProps>(({onClick}, ref) => {
    const [isClicked, setIsClicked] = useState(false);

    const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
        setIsClicked(!isClicked);
        if (onClick) onClick(e);
    };

    return (
        <button className={"lg:hidden"} ref={ref} onClick={(e) => handleClick(e)}>
            <div className={"grid justify-items-center gap-1"}>
                <span
                    className={`h-[3px] w-6 rounded-full bg-black transition duration-default ${
                        isClicked ? 'rotate-45 translate-y-2.5 bg-primary' : ''
                    }`}
                ></span>
                <span
                    className={`h-[3px] w-6 rounded-full bg-black transition duration-default ${
                        isClicked ? 'scale-x-0 bg-primary' : ''
                    }`}
                ></span>
                <span
                    className={`h-[3px] w-6 rounded-full bg-black transition duration-default ${
                        isClicked ? '-rotate-45 -translate-y-1 bg-primary' : ''
                    }`}
                ></span>
            </div>
        </button>
    );
});

export default Menu;
