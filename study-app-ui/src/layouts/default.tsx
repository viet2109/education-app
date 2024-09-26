import Header from "../components/header.tsx";
import Footer from "../components/footer.tsx";
import React from "react";

interface Props {
    children: React.ReactElement;
}

function DefaultLayout(props: Props) {
    const {children} = props;
    return (
        <>
            <Header/>
            <div className={"mt-20 px-default"}>
                {children}
            </div>
            <Footer/>
        </>
    );
}

export default DefaultLayout;