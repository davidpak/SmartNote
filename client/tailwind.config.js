/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    colors: {
      accent: '#375EF9', // emphasis blue
      'accent-dark': '#1B37A7',
      white: '#FFFFFF', // white
      black: '#000000', // black
      'neutral-100': '#FBFBFB', // grey 5
      'neutral-150': '#F5F5F5',
      'neutral-200': '#EFEFEF', // grey 4
      'neutral-300': '#D9D9D9', // grey 3
      'neutral-400': '#CCCCCC', // grey 2
      'neutral-500': '#37352F', // grey 1
      warning: '#B33B38',
      'warning-light': '#FFD3D3',
    },
    fontFamily: {
      sans: ['Inter', 'sans-serif'],
    },
    extend: {
      content: {
        checkIcon: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 18 18' fill='none'%3E%3Cpath d='M15 4.5L6.75 12.75L3 9' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E")`,
        minusIcon: `url("data:image/svg+xml,%3Csvg width='14' height='2' viewBox='0 0 14 2' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M1.75 1H12.25' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E%0A")`,
      },
    },
  },
  plugins: [],
};
