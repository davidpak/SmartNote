/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    colors: {
      accent: '#375EF9', // emphasis blue
      background: '#FFFFFF', // white
      'text-primary': '#000000', // black
      'text-secondary': '#636363', // grey 1
      'neutral-100': '#FBFBFB', // grey 5
      'neutral-200': '#EFEFEF', // grey 4
      'neutral-300': '#D9D9D9', // grey 3
      'neutral-400': '#CCCCCC', // grey 2
      warning: 'FFD3D3', // light rose red
    },
    fontFamily: {
      sans: ['Inter', 'sans-serif'],
    },
    extend: {},
  },
  plugins: [],
};
