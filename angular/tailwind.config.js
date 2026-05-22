/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{html,ts}',
  ],
  theme: {
    extend: {},
  },
  plugins: [],
  // Avoid conflicting with Angular Material's base styles
  corePlugins: {
    preflight: false,
  },
};
