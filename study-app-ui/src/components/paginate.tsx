import {
  MdKeyboardDoubleArrowLeft,
  MdKeyboardDoubleArrowRight,
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
      containerClassName="flex gap-x-3 mt-12 justify-center w-full mx-auto  py-4 px-6  shadow-custom rounded-lg"
      pageLinkClassName="w-10 aspect-square grid place-items-center hover:bg-gray-300  ease-in-out"
      pageClassName="rounded-full overflow-hidden min-w-fit"
      activeLinkClassName="bg-primary text-white hover:!bg-primary cursor-not-allowed"
      pageCount={Math.max(1, Math.ceil(itemsLength / numberItemOnPage))}
      previousLabel={<MdKeyboardDoubleArrowLeft size={28} />}
      nextLabel={<MdKeyboardDoubleArrowRight size={28} />}
      onPageChange={(e) => {
        onPageChange(e.selected);
      }}
      disabledLinkClassName="hover:bg-transparent hover:cursor-not-allowed text-gray-300"
      previousLinkClassName="w-10 aspect-square grid place-items-center hover:bg-gray-300  ease-in-out rounded-full"
      nextLinkClassName="w-10 aspect-square grid place-items-center hover:bg-gray-300  ease-in-out rounded-full"
    />
  );
}

export default Pagianate;
