# PitWall — Product Requirements & Overview Document (PRD.md)

**PitWall** (`com.jenil.f1comp`) is a modern, high-performance Formula 1 companion application designed for Android. It equips motorsport enthusiasts with live team radio transmissions, live session telemetry & timing towers, an AI-powered race strategy assistant ("Apex"), real-time session countdowns, full season schedules, live standings, detailed driver and constructor profiles, race results breakdown, curated news, cloud profile sync, and automated calendar & notification alerts.

---

## 1. Product Vision & Value Proposition

Formula 1 fans often need to navigate multiple disjointed platforms to keep up with race weekends — streaming apps for team radio, news sites for articles, calendar apps for session timings, social media for race control updates, and live timing apps for sector gaps. 

**PitWall** consolidates the complete Grand Prix experience into a single, offline-first application that delivers real-time motorsport intelligence, rich telemetry visuals, cloud profile synchronization, and smart system integration.

### Core Value Pillars
1. **Real-Time Race Intelligence**: Instant session status, track weather telemetry, timing towers, and grand prix countdowns in local time zones.
2. **Authentic Paddock Audio**: Direct stream access to live team radio audio clips between drivers and race engineers.
3. **AI-Driven Strategy Insights**: Conversational AI chatbot ("Apex") trained on technical regulations, standings, and driver H2H stats.
4. **Cloud Auth & Paddock Profile Sync**: Firebase Auth & Google Sign-In with real-time Firestore profile syncing, favorite driver/team preferences, and photo picker integration.
5. **Seamless System Integration**: System calendar syncing for all 24 Grand Prix weekend sessions and custom F1 engine sound alerts.
6. **Offline-First Reliability**: Complete Room database caching (Schema v51) ensures full app functionality without an active internet connection.

---

## 2. Target Audience & User Personas

| Persona | Description | Key Needs | Primary PitWall Features Used |
| :--- | :--- | :--- | :--- |
| **Die-Hard F1 Fan** | Watches every session (FP1 to Race), tracks lap times, and follows technical regulations. | Live radio audio, live timing tower, sector gaps, detailed qualifying sector times, H2H stats. | Team Radio Hub, Live Telemetry Dashboard, Race Results (Quali/Sprint), Teammate H2H Cards. |
| **Casual Viewer** | Focuses on Grand Prix race days, driver standings, and major news headlines. | Quick countdowns, standings overview, push alerts before race start. | Home Dashboard, Standings Screen, FCM Race Start Reminders. |
| **Global Track Enthusiast** | Attends races or follows international schedules across time zones. | Localized session timings, system calendar integration. | Calendar Syncing, Localized Session Timetable, Circuit Telemetry. |
| **Paddock Member** | Desires personalized favorite driver/team updates and tailored theme styling. | Cloud account sync, favorite constructor/racer alerts, customized team themes. | Cloud Auth, Paddock User Profile Hub, Favorite Team/Driver FCM Topics, Theme Customizer. |

---

## 3. Feature Specifications & Functionality

### 3.1 Live Team Radio Transmissions 📻
- **Description**: Access live radio audio clips transmitted between drivers and pit walls during Grand Prix sessions.
- **Key Capabilities**:
  - Direct streaming audio playback via Android `MediaPlayer`.
  - Interactive driver filter chips (`ALL`, `VER`, `NOR`, `LEC`, `HAM`, `RUS`, `ALO`, etc.).
  - Real-time progress bar with current vs total duration timers.
  - Team-colored player cards with custom team accent swatches.
  - Offline local caching via Room (`TeamRadioEntity`) so previously loaded clips remain playable offline.

---

### 3.2 Apex AI Chatbot Strategy Assistant 🤖
- **Description**: An AI assistant providing immediate answers regarding race strategy, technical rules, driver head-to-head records, and season schedules.
- **Key Capabilities**:
  - Quick prompt suggestions bar (e.g., "2026 Active Aero rules", "Driver Standings", "Next Race Schedule").
  - Pulse animation connection indicator and "thinking" state bubbles.
  - Chat session reset trigger to clear telemetry memory.
  - Direct integration with dedicated Chat API endpoint (`https://chatbot-9feg.onrender.com/`).

---

### 3.3 Home Dashboard & Live Session Timetable 🏎️
- **Description**: The central hub displaying the next upcoming Grand Prix, live countdown timer, and recent results.
- **Key Capabilities**:
  - Live session breakdown (FP1, FP2, FP3, Qualifying, Sprint, Main Race).
  - Track weather telemetry badge (air temperature, track temperature, weather condition).
  - Podium Hero Card showcasing P1, P2, and P3 finishers from the latest Grand Prix.
  - Top 3 Drivers and Constructors standings snapshot with direct navigation to full standings.

---

### 3.4 Live Telemetry & Timing Tower ⏱️
- **Description**: Real-time live timing tower and session telemetry telemetry hub during active race weekends.
- **Key Capabilities**:
  - **Timing Tower**: Positions, driver codes, gap to leader / interval times, sector split indicators (S1, S2, S3), and tyre compound visual badges (Soft, Medium, Hard, Intermediate, Wet).
  - **Race Control Messages**: Real-time race control notifications (Yellow Flags, Safety Car, DRS Enabled, Incident Investigations).
  - **Weather Metrics**: Live track temperature, air temperature, wind speed, and humidity indicators.
  - **Customization**: Telemetry header style toggles (Compact vs Default) and customizable timing tower column visibility settings in `TelemetrySettingsScreen`.

---

### 3.5 Cloud Authentication & Paddock Profile Hub 🔐👤
- **Description**: Secure cloud account authentication with Firebase Auth, Google OAuth, and Cloud Firestore profile synchronization.
- **Key Capabilities**:
  - **Firebase Auth & Google OAuth**: Sign-in with Email/Password or one-tap Google Sign-In powered by Android Credential Manager API.
  - **Password Security Spectrum**: Real-time 5-criteria password strength analyzer evaluating length (8+), uppercase, lowercase, numbers, and special characters with dynamic color spectrum bars and checkmark indicators.
  - **Targeted Field Highlighting**: Validation errors dynamically highlight specific input fields with F1 Red borders and trigger sleek top-anchored telemetry Snackbars.
  - **First-Launch Onboarding**: Smart detection redirects newly installed app instances to the Sign-In/Register onboarding flow before accessing Home.
  - **Cloud Firestore Profile Sync**: User profile document (`/users/{uid}`) mirrors display name, account email, avatar photo URL, favorite team, and favorite driver across devices.
  - **Redesigned Paddock User Profile**: Avatar halo ring, system gallery photo picker, interactive favorite constructor & driver dropdown selectors, help/support shortcuts (Email, WhatsApp Paddock Community, Play Store Reviews, Socials), and telemetry footer (*"Built for the fans with ❤️ in India 🇮🇳"*).

---

### 3.6 Grand Prix Weekend Schedule & System Calendar Sync 📅
- **Description**: Interactive 24-round season schedule with automated system calendar export.
- **Key Capabilities**:
  - Segmented tab toggle bar ("All Races", "Upcoming", "Completed") with smooth sliding pill indicators.
  - Expandable session timetables convert UTC session dates into the user's local device time zone.
  - Circuit telemetry cards displaying total laps, circuit length (km), circuit type (street vs permanent), and track layout SVG diagrams.
  - **Calendar Sync Engine**: One-tap export of all or upcoming Grand Prix weekend sessions (FP1-3, Quali, Sprint, Race) directly into Android's default primary calendar with duplicate prevention and batch clearing options.

---

### 3.7 Championship Standings & Driver/Team Profiles 🏆
- **Description**: Comprehensive World Drivers' Championship (WDC) and World Constructors' Championship (WCC) standings and analytics.
- **Key Capabilities**:
  - Animated toggle between Driver and Constructor standings.
  - Championship round progress indicator (e.g., `R14 / 24`).
  - Link out to official FIA points system breakdown.
  - **Driver Profile Cards**: Season points, wins, podiums, career stats, teammate H2H comparison, and interactive Vico points progression charts.
  - **Constructor Profile Cards**: Technical chassis specs, power unit supplier, team principal, base location, team biography, and driver lineup cards.

---

### 3.8 Race Results Breakdown 🏁
- **Description**: Detailed classification lists for every session of a Grand Prix weekend.
- **Key Capabilities**:
  - Round selector chips and session toggles (Race, Qualifying, Sprint, Sprint Qualifying).
  - Display final position, grid position changes (positions gained/lost), gap/time to leader, points scored, and fastest lap indicators.
  - Full support for Sprint weekend formats.

---

### 3.9 F1 News Hub 📰
- **Description**: Aggregated news feed from top motorsport media outlets.
- **Key Capabilities**:
  - Parse RSS/HTML news items using Jsoup and Retrofit.
  - Expandable news reader cards with full article preview descriptions and published relative time (`2h ago`, `Just now`).
  - One-tap button to open original full article in system web browser.

---

### 3.10 Push Notifications & Local Session Reminders 🔔
- **Description**: Customizable notifications keeping fans updated before and during race weekends.
- **Key Capabilities**:
  - **Firebase Cloud Messaging (FCM)**: Remote push channels for `breaking_news`, `live_race_events`, `standings_updates`, and favorite team/driver topics (`favorite_team_*`, `favorite_driver_*`). Rich notifications with `BigPictureStyle` article image downloads and custom F1 engine sound (`f1_car_2.mp3`).
  - **Local Exact Alarms (`AndroidAlarmScheduler`)**: Exact 15-minute start reminders for race sessions scheduled via Android `AlarmManager`.
  - **Boot Receiver (`RaceBootReceiver`)**: Automatically re-syncs all scheduled session alarms following a device reboot.

---

### 3.11 Adaptive Navigation & Personalization 🎨
- **Description**: Extensive UI theme customization and adaptive navigation support.
- **Key Capabilities**:
  - **Adaptive Navigation Rail**: Wide window and landscape displays seamlessly switch from bottom navigation bar (`F1BottomNavigationBar`) to side navigation rail (`F1NavigationRail`).
  - **Glassmorphic Navigation Bar**: Translucent floating bottom pill with Haze background blur effect.
  - **Dynamic Theme Engine**: Light and Dark mode switching with custom primary theme swatches (Classic Red, Midnight Teal, Paddock Orange, Racing Blue) and 11 team livery primary swatches.

---

## 4. Technical Product Specifications

| Property | Value |
| :--- | :--- |
| **App Name** | PitWall — Modern F1 Companion |
| **Package Name** | `com.jenil.f1comp` |
| **Version Code** | 3 |
| **Version Name** | `1.2.2` |
| **Minimum SDK** | API 26 (Android 8.0 Oreo) |
| **Target SDK** | API 37 (Android 15 / 16) |
| **Compile SDK** | API 37 (Minor 1) |
| **Primary Language** | 100% Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 + Haze Materials |
| **Architecture Pattern** | MVVM + Repository + Unidirectional Data Flow |
| **Dependency Injection** | Hilt (Dagger) with KSP |
| **Local Database** | Room Database v51 (14 Entities & DAOs) |
| **Key-Value Storage** | Jetpack DataStore Preferences |
| **Network Engine** | Retrofit 2 + OkHttp + Gson Converter |
| **Cloud Backend** | Firebase Auth + Cloud Firestore (`users/{uid}`) + FCM |
| **Authentication API** | Android Credential Manager API (`1.6.0`) + Google ID (`1.2.1`) |
| **Image Pipeline** | Coil with RAM & Disk caching |
| **Chart Analytics** | Vico Compose Charting Engine |
| **Push Notifications** | Firebase Cloud Messaging (FCM) + Android `AlarmManager` |

---

## 5. Release History & Version Roadmap

### Current Release: v1.2.2 🏎️🏁
- **Cloud Authentication**: Firebase Auth and Google OAuth integration via Android Credential Manager API.
- **Password Security Spectrum**: Interactive real-time 5-criteria password strength analyzer (8+ chars, upper, lower, number, special character) with color spectrum bars.
- **Targeted Error Highlighting**: Field validation highlighting with F1 Red borders and telemetry snackbars.
- **Smart First-Launch Onboarding**: Directs new app installs to Sign-In/Register before landing on Home.
- **Cloud Firestore Profile Sync**: Real-time user profile sync (`/users/{uid}`) storing callsigns, emails, avatars, favorite teams, and drivers.
- **Redesigned Paddock User Profile Hub**: Verified driver badges, avatar halo, system gallery photo picker, interactive favorite constructor & driver selectors, help/support shortcuts (Email, WhatsApp, Play Store, Socials), and telemetry footer.
- **Live Telemetry & Timing Tower**: Real-time timing tower, sector split indicators, tyre compound badges, race control messages, and weather metrics.
- **Adaptive Navigation Rail**: Added `F1NavigationRail` for wide and landscape form factor support alongside floating `F1BottomNavigationBar`.
- **Navigation Drawer & Layout Upgrades**: Scrollable drawer navigation items with pinned top header and bottom executive theme switch.
- **Expanded Settings Hub**: Added Telemetry Settings, Language Settings, Data Attribution, Privacy Policy, Terms & Conditions, and Licenses screens.
- **Room Database Schema v51**: Full offline caching across 14 tables.

### Previous Release: v1.2.1 🏎️
- **Schedule Hub Redesign**: Added sliding segmented tab toggle ("All Races", "Upcoming", "Completed"), compact expandable upcoming race cards, track layout SVGs, and circuit telemetry columns.
- **News Feed Upgrades**: Refresh actions and expanded card layouts with direct article CTA buttons.
- **Vico Chart Upgrades**: Enhanced point progression charts for driver/team profile analytics.

### Future Product Roadmap 🚀
1. **Interactive Fantasy F1 Helper**: AI-powered driver selection advice based on track history and weather forecasts.
2. **Wear OS Companion Tile**: Quick countdown tile and live race position updates on Wear OS smartwatches.
3. **Home Screen Widgets**: Glance-based Compose Android widgets for upcoming session countdowns and championship standings.
4. **WebSocket / SSE Live Streams**: Low-latency WebSocket streaming for real-time live timing during Grand Prix weekend sessions.
