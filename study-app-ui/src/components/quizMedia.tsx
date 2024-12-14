import Zoom from "react-medium-image-zoom";
import "react-medium-image-zoom/dist/styles.css";
import ReactPlayer from "react-player/lazy";
import "swiper/css";
import "swiper/css/navigation";
import { Navigation } from "swiper/modules";
import { Swiper, SwiperSlide } from "swiper/react";
import { Media } from "../types";
import { FaFileLines } from "react-icons/fa6";

interface Props {
  files: (Media | File)[];
  className?: string;
}

function QuizMedia({ files, className }: Props) {
  const groupedFiles = files.reduce((group, file) => {
    const mainType = (file as Media).fileType.split("/")[0];
    if (!group[mainType]) {
      group[mainType] = [];
    }
    group[mainType].push(file as Media);
    return group;
  }, {} as Record<string, Media[]>);

  return (
    <div className={`flex flex-col gap-y-8 cursor-auto ${className}`}>
      {/* Hiển thị nhóm image dưới dạng swiper */}
      {groupedFiles.image && groupedFiles.image.length > 0 && (
        <div>
          <h2 className="mb-3 text-sm italic">Images:</h2>

          <Swiper
            slidesPerView={1}
            navigation
            modules={[Navigation]}
            className="max-w-[1000px]"
            breakpoints={{
              768: {
                slidesPerView: 2,
              },
              1024: {
                slidesPerView: 3,
              },
              1200: {
                slidesPerView: 4,
              },
            }}
          >
            {groupedFiles.image.map((file) => (
              <SwiperSlide key={file.id}>
                <Zoom>
                  <div className="flex justify-center">
                    <img
                      src={file.fileUrl}
                      alt={file.filename}
                      className="w-20"
                    />
                  </div>
                </Zoom>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      )}

      {/* Hiển thị nhóm video dưới dạng swiper */}
      {groupedFiles.video && groupedFiles.video.length > 0 && (
        <div>
          <h2 className="mb-3 text-sm italic">Videos:</h2>
          <Swiper
            spaceBetween={30}
            slidesPerView={1}
            className="max-w-[1000px]"
            navigation
            modules={[Navigation]}
            breakpoints={{
              768: {
                slidesPerView: 2,
              },
              1024: {
                slidesPerView: 3,
              },
              1200: {
                slidesPerView: 4,
              },
            }}
          >
            {groupedFiles.video.map((file) => (
              <SwiperSlide key={file.id}>
                <div className="w-full *:!w-full *:!h-52 *:rounded-3xl *:overflow-hidden">
                  <ReactPlayer url={file.fileUrl} controls></ReactPlayer>
                </div>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      )}

      {/* Hiển thị nhóm file audio dưới dạng swiper */}
      {groupedFiles.audio && groupedFiles.audio.length > 0 && (
        <div>
          <h2 className="mb-3 text-sm italic">Audio:</h2>

          <Swiper
            slidesPerView={1}
            className="max-w-[1000px]"
            navigation
            modules={[Navigation]}
            breakpoints={{
              768: {
                slidesPerView: 2,
                spaceBetween: 30,
              },
              1024: {
                slidesPerView: 3,
                spaceBetween: 30,
              },
              1200: {
                slidesPerView: 4,
                spaceBetween: 30,
              },
            }}
          >
            {groupedFiles.audio.map((file) => (
              <SwiperSlide key={file.id}>
                <audio controls className="w-full">
                  <source src={file.fileUrl} />
                </audio>
              </SwiperSlide>
            ))}
          </Swiper>
        </div>
      )}

      {/* Hiển thị các loại file khác dưới dạng download link */}
      {Object.keys(groupedFiles).map((fileType) => {
        if (
          fileType !== "image" &&
          fileType !== "video" &&
          fileType !== "audio"
        ) {
          return (
            <div>
              <h2 className="mb-3 text-sm italic">Document files:</h2>

              <div className="flex flex-wrap gap-3">
                {groupedFiles[fileType].map((file) => (
                  <a
                    href={file.fileUrl}
                    download
                    className="bg-slate-300 p-4 rounded-full flex justify-center items-center gap-3 w-fit hover:text-white "
                  >
                    <FaFileLines size={20}></FaFileLines>
                    <span className="line-clamp-1 max-w-36">
                      {file.filename}
                    </span>
                  </a>
                ))}
              </div>
            </div>
          );
        }
        return null;
      })}
    </div>
  );
}

export default QuizMedia;
