import { IoIosArrowBack, IoIosArrowForward } from "react-icons/io";
import {
  MdKeyboardDoubleArrowLeft,
  MdKeyboardDoubleArrowRight,
  MdOutlineArrowLeft,
} from "react-icons/md";
import ReactPaginate from "react-paginate";

interface Props {
  onPageChange: (numberPage: number) => void;
  itemsLength: number;
  numberItemOnPage: number;
  initialPage: number;
}

function Pagianate(props: Props) {
  const { onPageChange, itemsLength, numberItemOnPage, initialPage } = props;

  return (
    <ReactPaginate
      forcePage={initialPage}
      containerClassName="transition-all duration-300 flex gap-x-3 mt-12 justify-center w-full mx-auto  py-4 px-6  shadow-custom rounded-lg"
      pageLinkClassName="transition-all duration-300 w-10 aspect-square grid place-items-center hover:bg-gray-300  ease-in-out"
      pageClassName="transition-all duration-300 rounded-full overflow-hidden min-w-fit"
      activeLinkClassName="transition-all duration-300 bg-primary text-white hover:!bg-primary cursor-not-allowed"
      pageCount={Math.max(1, Math.ceil(itemsLength / numberItemOnPage))}
      previousLabel={<IoIosArrowBack size={28} />}
      nextLabel={<IoIosArrowForward size={28} />}
      onPageChange={(e) => {
        onPageChange(e.selected);
      }}
      disabledLinkClassName="transition-all duration-300 hover:bg-transparent hover:cursor-not-allowed text-gray-300"
      previousLinkClassName="transition-all duration-300 w-10 aspect-square grid place-items-center hover:bg-gray-300  ease-in-out rounded-full"
      nextLinkClassName="transition-all duration-300 w-10 aspect-square grid place-items-center hover:bg-gray-300  ease-in-out rounded-full"
    />
  );
}

export default Pagianate;
