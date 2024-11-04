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
                dot1: "loader 1s infinite alternate 0.2s",
                dot2: "loader 1s infinite alternate 0.4s",
                dot3: "loader 1s infinite alternate 0.6s",
                dot4: "loader 1s infinite alternate 0.8s",
                dot5: "loader 1s infinite alternate 1s",
            },

            keyframes: {
                fadeIn: {
                    from: {opacity: 0},
                    to: {opacity: 1},
                },
                loader: {
                    "0%": {
                        width: "2px",
                        height: "2px",
                        borderRadius: "1px",
                    },
                    "100%": {
                        width: "20px",
                        height: "20px",
                        borderRadius: "10px",
                    },
                },
            },
        },
    },
    plugins: [],
}

