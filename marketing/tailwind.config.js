/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        'hornswoggled-purple': '#7B2CBF',
        'hornswoggled-pink': '#FF006E',
        'hornswoggled-orange': '#FF9E00',
        'hornswoggled-blue': '#3A86FF',
        'hornswoggled-green': '#06FFA5',
      },
    },
  },
  plugins: [],
}
