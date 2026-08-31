<p align="center">
  <img src="assets/app_icon.png" width="180" alt="PitWall App Icon" />
</p>

<h1 align="center">PitWall — A Modern F1 Companion for Android</h1>

<p align="center">
  PitWall is a sleek, high-performance Android application for Formula 1 fans that delivers real-time session status, season schedules, live standings, detailed driver/team profiles, race results, curated news, and customizable alerts — all in a lightweight, offline-first package.
</p>

---

## Screenshots

<p align="center">
  <img src="assets/screenshots/screenshot-01-home.png" width="280" alt="Home — next race countdown and live session" />
  <img src="assets/screenshots/screenshot-02-schedule.png" width="280" alt="Full season schedule with session times" />
  <img src="assets/screenshots/screenshot-03-standings.png" width="280" alt="Driver and Constructor standings" />
</p>

---

## Key Features ✨

- **Real-Time Home Dashboard**
  - Next race countdown with session breakdown (FP1, FP2, FP3, Qualifying, Sprint, Race).
  - Quick glance at recent race podiums and track weather snapshots.
  - Top standings overview for Drivers and Constructors.

- **FCM Push Notifications & Session Reminders**
  - Instant breaking news, race control updates, and championship standings alerts via Firebase Cloud Messaging (FCM).
  - Local exact-alarm scheduler (`AndroidAlarmScheduler`) for custom session start reminders (15m, 30m, 1h prior).
  - Custom F1 engine audio notifications and rich expanded notification styles.

- **Dynamic Driver & Team Profiles**
  - Deep-dive driver stats: season points, podiums, wins, and head-to-head teammate comparisons.
  - Points progression charts and driver profile cards.
  - Constructor profiles: chassis technical specifications, power units, team leadership, and car imagery.

- **Full Season Schedule & Race Results**
  - Interactive Grand Prix weekend schedules with local session timing.
  - Detailed race results screen with final positions, grid changes, and fastest laps.

- **Smart System Calendar Sync**
  - Export Grand Prix weekend schedules directly to your device's system calendar with options for future races or complete clearing.

- **F1 News Hub**
  - Aggregated and parsed headlines from top F1 media sources with expandable reader cards.

- **Premium Glassmorphism UI & Customization**
  - Modern Glassmorphism blur effects powered by the Haze library.
  - Seamless Light & Dark mode switching with custom theme accent pickers.

- **Offline-First Architecture**
  - Robust local persistence with Room Database ensures data availability even without connectivity.

---

## Stack & Libraries

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material 3 & Glassmorphism (Haze)
- **Dependency Injection:** Hilt (`com.google.dagger:hilt-android`) with KSP
- **Architecture:** MVVM + Repository pattern with Kotlin Coroutines & StateFlow
- **Database / Local Storage:** Room (`androidx.room`) & Jetpack DataStore Preferences
- **Networking:** Retrofit + OkHttp logging interceptor + Gson converter
- **Push Notifications & Alarms:** Firebase Cloud Messaging (FCM) & Android `AlarmManager`
- **Media & Parsing:** Coil (Image loading) & Jsoup (HTML news parsing)

---

## Project Structure

```
app/src/main/java/com/jenil/f1comp/
├── data/
│   ├── local/        — Room Database, DAOs, and entities (offline cache)
│   ├── model/        — Domain data models
│   ├── remote/       — Retrofit API service interfaces
│   └── repository/   — Single source of truth & data orchestration
├── di/               — Hilt dependency injection modules
├── notification/     — FCM service, alarm scheduler, and notification channels
├── ui/
│   ├── components/   — Reusable Compose UI components
│   ├── home/         — Home dashboard & countdown screen
│   ├── navigation/   — Navigation host, routes & bottom bar
│   ├── news/         — News reader screen & expandable cards
│   ├── profile/      — Driver & Team profile screens, charts & comparisons
│   ├── results/      — Detailed race result breakdown
│   ├── schedule/     — Race weekend schedule & calendar sync
│   ├── settings/     — Notification preferences & theme customizer
│   ├── standings/    — Driver & Constructor standings screens
│   └── theme/        — Material 3 color palettes, typography & theme picker
├── viewmodel/        — ViewModels handling state logic
└── util/             — Mappers, date formatting & calendar sync helpers
```

---

## Requirements

- **Android Studio:** 2024.2 "Ladybug" or newer
- **JDK:** 11 / 17 / 21 (configured for Kotlin JVM toolchain)
- **Android SDK:** Min SDK 26 (Android 8.0), Target/Compile SDK 36/37

---

## Quick Start (Clone, Build, Run)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/JenilMacwan/Pitwall.git
   cd Pitwall
   ```

2. **Build from the command line or open in Android Studio:**
   - Assemble Debug APK:
     ```bash
     ./gradlew :app:assembleDebug
     ```
   - Install on connected device:
     ```bash
     ./gradlew :app:installDebug
     ```

3. **Run Unit Tests:**
   ```bash
   ./gradlew test
   ```

---

## Configuration

- **Firebase Setup:** Place your `google-services.json` file inside the `app/` directory for Firebase Cloud Messaging functionality.
- **Notification Permissions:** Android 13+ (API 33+) requires post-notification runtime permission for race alerts.

---

## License

This project is licensed under the MIT License — see the LICENSE file for details.

Developed with ❤️ for F1 fans by **Jenil Macwan**.
