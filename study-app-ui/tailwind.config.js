/** @type {import('tailwindcss').Config} */
export default {
    content: ["./src/**/*.{js,jsx,ts,tsx}"],
    theme: {
        extend: {
            colors: {
                primary: "#27b489"
            },
            fontFamily: {
                quickSan: ["quick-san", "san-serif"]
            },
            boxShadow: {
                custom: "0 .5pt 10pt rgba(0, 0, 0, .1);",
            },
            transitionDuration: {
                default: "250ms"
            },
            maxWidth: {
                default: "1280px"
            },
            animation: {
                fade: 'fadeIn .75s ease-in-out',
            },

            keyframes: {
                fadeIn: {
                    from: {opacity: 0},
                    to: {opacity: 1},
                },

            }
        },
    },
    plugins: [],
}

