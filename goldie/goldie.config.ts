const APP_ROOT = "C:/Users/jenil/OneDrive/Desktop/F1Comp";
const APK = `${APP_ROOT}/app/build/outputs/apk/debug/app-debug.apk`;

const config = {
  appRoot: APP_ROOT,
  appPath: APK,
  bundleId: "com.jenil.f1comp",
  android: {
    appPath: APK,
    applicationId: "com.jenil.f1comp",
  },
  devices: ["pixel-10-pro"],
  locales: ["en-US"],
  appearance: "dark",
  frame: { variant: "17-pro-blue" },
  theme: {
    background: "linear-gradient(145deg, #10090A 0%, #281014 48%, #10090A 100%)",
    headlineColor: "#FFFFFF",
    subheadColor: "#E2C9CC",
    fontFamily: "Montserrat",
    copyHeightRatio: 0.22,
    deviceWidthRatio: 0.78,
    template: ["hero", "classic", "classic", "classic", "classic"],
    layout: "classic",
  },
  store: {
    name: "PitWall",
    subtitle: { "en-US": "F1 race companion" },
    developer: "PitWall",
    category: "Sports",
    rating: 4.8,
    ratingCount: "Race fans",
    ageRating: "Everyone",
    price: "Free",
    description: {
      "en-US": "Stay close to every Formula 1 weekend with race schedules, driver and constructor standings, race results, news and team radio in one companion app.",
    },
  },
  scenes: [
    {
      kind: "screenshot",
      id: "home",
      flow: "store-01-home",
      headline: { "en-US": "Your race weekend, closer" },
      subhead: { "en-US": "The next Grand Prix and the details fans follow." },
    },
    {
      kind: "screenshot",
      id: "standings",
      flow: "store-02-standings",
      headline: { "en-US": "Follow every position" },
      subhead: { "en-US": "Driver and team standings, at a glance." },
    },
    {
      kind: "screenshot",
      id: "schedule",
      flow: "store-03-schedule",
      headline: { "en-US": "Never miss a session" },
      subhead: { "en-US": "Plan the full Grand Prix weekend in your local time." },
    },
    {
      kind: "screenshot",
      id: "results",
      flow: "store-04-results",
      headline: { "en-US": "Relive every race" },
      subhead: { "en-US": "Explore finishing order and the story in the gaps." },
    },
    {
      kind: "screenshot",
      id: "news",
      flow: "store-05-news",
      headline: { "en-US": "Stay in the paddock" },
      subhead: { "en-US": "Catch up on the latest stories across the season." },
    },
  ],
};

export default config;
