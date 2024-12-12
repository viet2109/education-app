import { Link } from "react-router-dom";
import { images } from "../assets/images";
import routers from "../configs/routers";
import { ReactElement } from "react";

interface Props {
  title?: string;
  className?: string;
  customBtn?: ReactElement;
}

function NoDataModel({ title = "No Data found", className, customBtn }: Props) {
  return (
    <div className={`pt-8 grid place-items-center ${className}`}>
      <div className="shadow-custom flex flex-col gap-y-4 items-center py-16 w-full max-w-[720px] rounded-2xl">
        <img src={images.noData} className="w-48" alt="no_data" />
        <span className="text-2xl px-8 text-center text-slate-400">
          {title}
        </span>
        {customBtn ? (
          customBtn
        ) : (
          <Link to={routers.home} className="btn-custom mt-4">
            Go home
          </Link>
        )}
      </div>
    </div>
  );
}

export default NoDataModel;
