import {
  FaGamepad,
  FaGraduationCap,
  FaPaperPlane,
  FaPlane,
  FaStar,
} from "react-icons/fa6";
import { images } from "../assets/images";

function Home() {
  return (
    <div>
      {/* banner */}
      <div className="md:flex justify-center md:gap-8 items-center">
        <div className="w-8/12 lg:w-5/12 mx-auto lg:mx-0 flex justify-center items-center">
          <img
            className="w-full lg:w-9/12"
            src={images.banner1}
            alt="banner1"
          />
        </div>
        <div className="text-center *:my-2 md:*:my-4 md:text-left lg:max-w-lg">
          <p className="text-xl  md:text-2xl lg:text-3xl">
            Play. Pass. Progress.
          </p>
          <p className="text-lg  md:text-xl lg:text-2xl md:!leading-loose">
            Prepare for local and international exams for free. Forever.
          </p>
          <button className="btn-custom md:!mt-1">Take your first test</button>
        </div>
      </div>
      {/* des 1 */}
      <div className="my-12">
        <h1 className="text-center text-lg md:text-2xl font-semibold">
          The best way to pass any exam across Viet Nam!
        </h1>
        <div className="my-12 flex flex-col md:flex-row">
          {/* top */}
          <div className="flex flex-1 flex-col gap-8">
            <div className="flex gap-x-8">
              <div>
                <FaPlane className="text-primary" size={24} />
              </div>
              <div>
                <h2 className="font-semibold mb-2 md:text-lg">
                  Enjoy personalised practice
                </h2>
                <p>
                  Enjoy personalised practice Exambly’s unlimited exam practice
                  adapts to your learning style with tailored tests that help
                  you grasp and review exercises intuitively.
                </p>
              </div>
            </div>
            <div className="flex gap-x-8">
              <div>
                <FaGamepad className="text-primary" size={24} />
              </div>
              <div>
                <h2 className="font-semibold mb-2 md:text-lg">
                  Gamified experience with rewards
                </h2>
                <p>
                  Earn virtual wealth, unlock new levels, and watch your exam
                  scores soar as you master new concepts and pass with flying
                  colours.
                </p>
              </div>
            </div>
          </div>

          {/* center */}
          <div className="flex-1 flex justify-center items-center">
            <img
              className="w-10/12"
              src={images.banner2}
              alt="banner2"
            />
          </div>

          {/* bottom */}
          <div className="flex flex-1 flex-col gap-8">
            <div className="flex gap-x-8">
              <div>
                <FaGraduationCap className="text-primary" size={24} />
              </div>
              <div>
                <h2 className="font-semibold mb-2 md:text-lg">
                  Learn smart with instant results
                </h2>
                <p>
                  You’ll quickly see which answers you get correct, and we'll
                  instantly show you how to improve more effectively, should you
                  miss an exercise.
                </p>
              </div>
            </div>
            <div className="flex gap-x-8">
              <div>
                <FaStar className="text-primary" size={24} />
              </div>
              <div>
                <h2 className="font-semibold mb-2 md:text-lg">
                  Achieve unlimited success
                </h2>
                <p>
                  Multiply your success with free exam practice anytime – no
                  hidden fees, no premium content, all for you, forever.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
