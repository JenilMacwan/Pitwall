# PitWall — Architecture & System Design Document

This document defines the system architecture, data flow rules, component dependencies, networking policies, database schema design, background notification subsystem, cloud authentication engine, security standards, and scalability guidelines for the **PitWall** Android application (`com.jenil.f1comp`).

---

## 1. System Overview

**PitWall** is an offline-first motorsport telemetry and session tracking application built for Formula 1 fans. The system delivers real-time session timing, live team radio audio transmissions, driver/team championship standings, AI strategy insights ("Apex"), cloud user authentication & Firestore profile synchronization, custom FCM push notifications, exact race alarms via Android's `AlarmManager`, and automated calendar syncing via system `ContentResolver`.

### Architectural Vision & Principles
- **Offline-First Cache Strategy**: The local Room Database (v51) is the single source of truth for all persistent UI state. Screens display cached data instantly without waiting for network API round-trips.
- **Unidirectional Data Flow (UDF)**: UI state flows down via immutable `StateFlow` streams from ViewModels. User actions flow up through explicit suspended ViewModel calls.
- **Strict Layer Separation**: UI composables never access network endpoints or database instances directly; all data access is orchestrated through Hilt-injected Repositories.
- **Reactive Stream Coupling**: Repositories observe local Room DAOs and return reactive `Flow<T>` streams, ensuring automatic UI updates whenever local database records change.
- **Cloud Account Synchronization**: User preferences (favorite team, favorite driver, callsign, avatar URL) seamlessly mirror between local DataStore Preferences, Firebase Auth, and Cloud Firestore (`/users/{uid}`).

---

## 2. Technology Stack & Dependencies

```
+-------------------------------------------------------------------+
|                        APPLICATION LAYER                          |
| Jetpack Compose • Material 3 • Haze Materials • Vico Charts • Rail|
+-------------------------------------------------------------------+
                                  │
                                  ▼
+-------------------------------------------------------------------+
|                      STATE & PRESENTATION LAYER                    |
|      Hilt ViewModels • StateFlow / Flow • Lifecycle Coroutines    |
+-------------------------------------------------------------------+
                                  │
                                  ▼
+-------------------------------------------------------------------+
|                         REPOSITORY LAYER                          |
|  Data Orchestration • Auth/Cloud Sync • Caching Policy • Errors  |
+---------------------------------+---------------------------------+
                                  │
                  ┌───────────────┴───────────────┐
                  ▼                               ▼
+-----------------------------------+   +---------------------------+
|          LOCAL STORAGE            |   |      REMOTE BACKENDS      |
|  Room DB (v51) • DataStore Prefs  |   | Retrofit 2 • Firebase Auth|
+-----------------------------------+   | Firestore • FCM • Chatbot |
                                        +---------------------------+
```

### Core Stack Specifications

| Category | Library / Tool | Version / Details | Purpose |
| :--- | :--- | :--- | :--- |
| **Language & Toolchain** | Kotlin 2.0+ / JVM 11 | `compileSdk 37`, `minSdk 26`, `targetSdk 37` | Application code & coroutines runtime (`v1.2.2`). |
| **UI Framework** | Jetpack Compose | Compose BOM (Material 3) | Declarative UI rendering & WindowSizeClass adaptive layouts. |
| **Glassmorphic UI** | Haze Materials | `dev.chrisbanes.haze` | Translucent blur effects on bottom navigation bar & rail. |
| **Charts & Graphs** | Vico Compose | `3.3.1` | Points progression line charts in driver profiles. |
| **Image Loading** | Coil Compose | `coil-compose` | Image fetching, disk/memory caching, and prefetching. |
| **Dependency Injection** | Hilt / Dagger KSP | `hilt-android`, `ksp` compiler | Dependency injection graph (`SingletonComponent`). |
| **Local Database** | Room Database | Version `51` (KSP compiler) | Offline relational cache (14 DAOs, 14 Entities). |
| **Key-Value Storage** | Jetpack DataStore | Preferences DataStore (`settings.preferences_pb`) | User preferences, theme modes, & notification flags. |
| **HTTP Client & REST** | Retrofit 2 / OkHttpClient | `retrofit2:converter-gson`, `logging-interceptor` | REST API service interfaces & network serialization. |
| **Cloud Services** | Google Firebase | BOM (Auth, Firestore, Messaging FCM, Analytics) | Cloud authentication, profile sync, & FCM push notifications. |
| **Auth & Credentials** | Credential Manager | `androidx.credentials:1.6.0`, Google ID `1.2.1` | One-tap Google Sign-In & credential management. |
| **Html/RSS Parsing** | Jsoup | `jsoup` | News feed content extraction and parsing. |
| **Unit Testing** | MockK + Turbine + JUnit | `mockk`, `turbine`, `kotlinx-coroutines-test` | Unit testing ViewModels, Repositories, and StateFlows. |

---

## 3. Architecture Layers & Data Flow

### Unidirectional Data Flow (UDF) Architecture

```
                    ┌──────────────────────────────────────┐
                    │          Jetpack Compose             │
                    │         (Screen Composable)          │
                    └──────────────────┬───────────────────┘
                                       │
                         User Event    │    Observes UI State
                        (e.g. Refresh) │    (collectAsStateWithLifecycle)
                                       ▼    ▲
                    ┌──────────────────┴────┴──────────────┐
                    │            Hilt ViewModel            │
                    │        (Exposes StateFlow<T>)        │
                    └──────────────────┬───────────────────┘
                                       │
                      Invokes suspended│    Returns Flow<List<Entity>>
                        repository     │    DAO Stream / Cloud Stream
                                       ▼    ▲
                    ┌──────────────────┴────┴──────────────┐
                    │          Repository Layer            │
                    │  (Cache-First / Cloud Sync Engine)   │
                    └──────────┬─────────────────┬─────────┘
                               │                 │
     Writes to DB / DataStore  │                 │ Network Request
     (upsert/insert/store)     │                 │ (getSchedule/Firestore/Auth)
                               ▼                 ▼
                    ┌──────────────────┐  ┌──────────────────┐
                    │  Room Database   │  │  Retrofit APIs & │
                    │   (14 Tables)    │  │ Firebase Cloud   │
                    └──────────────────┘  └──────────────────┘
```

### Cache-First Synchronization Strategy
1. **Initial Rendering**: Screen composables collect ViewModel `StateFlow` instances initialized via `repository.getCachedData()`. Room Database immediately emits cached records from SQLite disk storage.
2. **Background Sync**: During screen initialization or explicit user pull-to-refresh events, ViewModels launch coroutines to execute suspended `repository.refreshData()` calls.
3. **Network Fetch & Upsert**: The repository executes HTTP requests via `F1ApiService`, maps JSON Response DTOs to Room Entity data classes, and executes upsert/insert operations within Room inside a database transaction (`OnConflictStrategy.REPLACE`).
4. **Automatic UI Emission**: Because Room DAO methods return `Flow<List<Entity>>`, database writes trigger an immediate emission on the observing Flow stream, updating the UI state cleanly without manual state invalidation.

---

## 4. Project Structure & Module Organization

The codebase follows a package-by-feature and package-by-layer hybrid structure within the `:app` module:

```
com.jenil.f1comp
├── F1Application.kt                     # Application entry point (@HiltAndroidApp)
├── MainActivity.kt                      # Activity host (@AndroidEntryPoint, edge-to-edge, FCM log)
│
├── data/
│   ├── PreferencesKeys.kt               # DataStore Preference Key definitions
│   ├── local/
│   │   ├── AppDatabase.kt               # Room Database definition (v51, 14 entities & DAOs)
│   │   ├── Converters.kt                # TypeConverters for custom objects & lists
│   │   ├── dao/                         # 14 Room Data Access Objects (DAOs)
│   │   │   ├── ScheduleDao.kt
│   │   │   ├── DriverStandingsDao.kt
│   │   │   ├── ConstructorStandingsDao.kt
│   │   │   ├── RaceResultDao.kt
│   │   │   ├── NextRaceDao.kt
│   │   │   ├── NewsDao.kt
│   │   │   ├── TeamRadioDao.kt
│   │   │   ├── QualifyingResultDao.kt
│   │   │   ├── SprintResultDao.kt
│   │   │   ├── SprintQualiDao.kt
│   │   │   ├── TeammateH2HDao.kt
│   │   │   ├── DriverProfileDao.kt
│   │   │   ├── ConstructorProfileDao.kt
│   │   │   └── CircuitInfoDao.kt
│   │   └── entity/                      # 14 Room Entity data classes
│   ├── model/                           # Domain DTOs, UserProfile, LiveTelemetry, ChatRequest
│   ├── remote/
│   │   ├── F1ApiService.kt              # Main REST service interface
│   │   └── ChatApiService.kt            # Apex AI strategy chatbot REST interface
│   └── repository/                      # 16 Repositories orchestrating cache, network & cloud
│       ├── AuthRepository.kt            # Firebase Auth & Cloud Firestore profile sync
│       ├── DataStoreRepository.kt       # Preferences DataStore & theme settings
│       ├── ScheduleRepository.kt
│       ├── DriverStandingsRepository.kt
│       ├── ConstructorStandingsRepository.kt
│       ├── RaceResultRepository.kt
│       ├── NextRaceRepository.kt
│       ├── NewsRepository.kt
│       ├── TeamRadioRepository.kt
│       ├── ChatBotRepository.kt
│       ├── QualifyingResultRepository.kt
│       ├── SprintResultRepository.kt
│       ├── SprintQualiResultRepository.kt
│       ├── TeammateHeadtoHeadRepository.kt
│       ├── DriverProfileRepository.kt
│       ├── ConstructorProfileRepository.kt
│       ├── LiveTelemetryRepository.kt
│       └── CircuitInfoRepository.kt
│
├── di/                                  # Hilt Dependency Injection Modules
│   ├── DatabaseModule.kt                # Room DB & DAO providers
│   ├── NetworkModule.kt                 # OkHttpClient & Retrofit instances
│   ├── DataStoreModule.kt              # Jetpack DataStore provider
│   ├── FirebaseModule.kt                # Firebase Auth & Firestore providers
│   └── AuthModule.kt                    # Auth Repository provider
│
├── notification/                        # Push & Exact Alarm Subsystem
│   ├── PitwallMessagingService.kt       # FCM Push Notification Receiver & builder
│   ├── AndroidAlarmScheduler.kt         # AlarmManager exact alarm scheduler
│   ├── RaceNotificationCoordinator.kt  # Calendar session alarm coordinator
│   ├── RaceAlarm.kt                     # Alarm item models & receivers
│   └── Reciever.kt                      # RaceBootReceiver (BOOT_COMPLETED handler)
│
├── ui/                                  # Presentation Layer (Jetpack Compose)
│   ├── F1ScreenPadding.kt               # System bar inset calculators
│   ├── auth/                            # Sign-In, Register, Password Spectrum, PillButton
│   ├── chatbot/                         # Apex AI assistant chat interface
│   ├── favorites/                       # Favorites selection components
│   ├── home/                            # Next race card, podium hero, top standings
│   ├── navigation/                      # NavHost, AppNavigation, Drawer, BottomBar, NavigationRail
│   ├── news/                            # News reader screen & expandable cards
│   ├── profile/                         # UserProfileScreen (Paddock Hub), Driver/Team profiles, Vico charts
│   ├── radio/                           # Live team radio streaming player
│   ├── results/                         # Race, Qualifying, & Sprint result tables
│   ├── schedule/                        # Season schedule & calendar sync
│   ├── settings/                        # Settings, Telemetry Settings, Languages, Licenses, Terms, Theme
│   ├── standings/                       # WDC / WCC standings screens
│   ├── state/                           # UI State data holders
│   ├── telemetry/                       # TelemetryScreen, Timing Tower, Race Control, Weather Cards
│   └── theme/                           # Color tokens, typography, team swatches, theme builders
│
├── util/                                # Utility Engine Classes
│   ├── TeamUtils.kt                     # Team color, code, logo & image resolvers
│   ├── ProfileUtils.kt                  # Flag CDN, emoji conversion, country codes
│   ├── NewsDateUtil.kt                  # Relative date formatters
│   ├── NotificationTimeUtil.kt          # Epoch timestamp calculators
│   └── ScheduleSync.kt                  # System ContentResolver Calendar synchronizer
│
└── viewmodel/                           # Hilt-Injected ViewModels
    ├── AuthViewModel.kt
    ├── SettingsViewModel.kt
    ├── LiveTelemetryViewModel.kt
    ├── NextRaceViewModel.kt
    ├── ScheduleViewModel.kt
    ├── StandingsViewModel.kt
    ├── NewsViewModel.kt
    ├── TeamRadioViewModel.kt
    ├── ChatViewModel.kt
    ├── RaceResultViewModel.kt
    ├── DriverProfileViewModel.kt
    └── ConstructorProfileViewModel.kt
```

---

## 5. Navigation & Routing Architecture

Navigation is handled by a central `AppNavigation` host enforcing First-Launch Onboarding logic and adaptive navigation rendering.

```
App Navigation Graph
├── First Launch Check (isFirstLaunch StateFlow)
│   ├── true  ──> Start at "login"
│   └── false ──> Start at "home"
│
├── Primary Bottom / Rail Destinations
│   ├── "home"                 ──> HomeScreen (Dashboard, Next Race Hero, Podium)
│   ├── "standings"            ──> StandingsScreen (WDC / WCC Toggles)
│   ├── "schedule"             ──> ScheduleScreen (24-Round Schedule, Calendar Sync)
│   └── "news"                 ──> NewsScreen (F1 News Feed & Article Reader)
│
├── Drawer & Secondary Destinations
│   ├── "login"                ──> LoginScreen (Sign-In Tab)
│   ├── "register"             ──> LoginScreen (Register Tab with Password Spectrum)
│   ├── "user_profile"         ──> UserProfileScreen (Paddock User Hub, Photo Picker, Favorites)
│   ├── "telemetry"            ──> TelemetryScreen (Live Timing Tower, Race Control, Weather)
│   ├── "team_radio"           ──> TeamRadioScreen (Live Audio Streams)
│   ├── "chatbot"              ──> ChatbotScreen (Apex AI Assistant)
│   ├── "race_result/{round}/{year}" ──> RaceResultScreen
│   ├── "profile/{isDriver}/{id}"    ──> ProfileScreen (Driver/Team detailed analytics)
│   ├── "settings"             ──> SettingsScreen
│   ├── "telemetry_settings"   ──> TelemetrySettingsScreen
│   ├── "theme_settings"       ──> ThemeSettingsScreen
│   ├── "language_settings"    ──> LanguageSettingsScreen
│   ├── "data_attribution"     ──> DataAttributionScreen
│   ├── "privacy_policy"       ──> PrivacyPolicyScreen
│   ├── "terms_and_conditions" ──> TermsAndConditionsScreen
│   └── "licenses"             ──> LicensesScreen
```

### Adaptive Window Navigation Logic
- **Phone Vertical Viewports**: Displays floating `F1BottomNavigationBar` with translucent Haze glassmorphism.
- **Wide / Landscape Viewports**: Switches to `F1NavigationRail` alongside the main content frame, conserving vertical screen real estate.

---

## 6. Subsystem Architectural Deep Dives

### 1. Database Subsystem (`AppDatabase`)
- **Version**: `51`
- **Fallback Strategy**: `.fallbackToDestructiveMigration(true)` resets cache tables when schema entity definitions change.
- **Type Converters**: `Converters.kt` uses Gson serialization to store nested objects (e.g., `RaceCountdown`, `RaceWeather`, `DriverInfo`, `ComparisonSet`) inside Room SQLite columns.

### 2. Networking Subsystem (`NetworkModule`)
- **Main F1 Retrofit Instance** (`@Named("F1Retrofit")`):
  - Base URL: `https://f1-companion-api-ba5k.onrender.com/`
  - Default OkHttpClient timeouts (10s connect/read).
- **Chatbot Retrofit Instance** (`@Named("ChatRetrofit")`):
  - Base URL: `https://chatbot-9feg.onrender.com/`
  - Extended OkHttpClient timeouts (**60 seconds**) to accommodate AI LLM inference generation cycles.
  - Header Injection: `X-API-Key` injected into request header via `CHAT_API_KEY` `BuildConfig` field.

### 3. Cloud Authentication & Profile Subsystem (`AuthRepository`)
- **Firebase Auth + Google OAuth**: One-tap Google Sign-In via Android Credential Manager API (`1.6.0`) and Google ID (`1.2.1`).
- **5-Criteria Password Validation**:
  ```kotlin
  require(
      password.length >= 8 &&
      password.any { it.isUpperCase() } &&
      password.any { it.isLowerCase() } &&
      password.any { it.isDigit() } &&
      password.any { !it.isLetterOrDigit() }
  )
  ```
- **Cloud Firestore Profile Mirror (`/users/{uid}`)**: Real-time snapshot listener (`currentUserProfileFlow`) emits updated `UserProfile` data classes containing display name, email, avatar photo URL, favorite team, and favorite driver across devices.

### 4. Live Telemetry Subsystem (`LiveTelemetryRepository`)
- **Endpoint**: `F1ApiService.getLiveTiming()` returns `LiveTelemetryResponse`.
- **Timing Tower Data**: Positions, driver codes, intervals, sector split indicators, tyre compound visual chips.
- **Race Control Stream**: Flag status, safety car deployment, incident reports.
- **User Preference Controls**: Toggle visibility for gaps, sector splits, and tyre compounds stored in DataStore Preferences and exposed via `SettingsViewModel`.

### 5. Push Notification & Alarm Subsystem
- **Firebase Cloud Messaging**: `PitwallMessagingService` handles FCM notifications and topic subscriptions (`breaking_news`, `live_race_events`, `standings_updates`, `favorite_team_*`, `favorite_driver_*`). Custom notification channel (`f1_alerts_v4`) triggers `BigPictureStyle` or `BigTextStyle` notifications with custom exhaust audio (`R.raw.f1_car_2`).
- **Exact Alarms (`AndroidAlarmScheduler`)**: Schedules precise 15-minute pre-session reminders via Android `AlarmManager.setExactAndAllowWhileIdle()` targeting `RTC_WAKEUP`.
- **Boot Recovery (`RaceBootReceiver`)**: Listens for `Intent.ACTION_BOOT_COMPLETED`, recovers user alarm preferences from DataStore, fetches cached schedule from Room, and re-schedules session alarms asynchronously.

### 6. System Calendar Sync Subsystem (`ScheduleSync.kt`)
- **Engine**: Queries Android system `CalendarContract.Events` content provider via `ContentResolver`.
- **Sync Logic**: Matches existing events with custom title tag `[PitWall]`. Inserts 24 Grand Prix race events with 15-minute reminders without duplicating existing entries.

---

## 7. Security Architecture & Standards

1. **Authentication Policy**: Firebase Auth tokens with email verification triggers (`sendEmailVerification()`).
2. **Password Security Spectrum Enforcement**: Validates 8+ length, uppercase, lowercase, numbers, and special characters prior to network transmission.
3. **API Key Confidentiality**: `CHAT_API_KEY` injected through environment variables or `local.properties` via Gradle `buildConfigField`, keeping secret keys out of source control repositories.
4. **Secure Transport**: All REST endpoints strictly enforce HTTPS (`https://`).

---

## 8. Scalability & System Performance Guidelines

### Concurrency & Coroutines
- All disk, network, and cloud Firestore operations run explicitly on `Dispatchers.IO`.
- ViewModel state flows are bound to `viewModelScope` with `.stateIn(started = SharingStarted.WhileSubscribed(5000))` to conserve resources when UI composables leave the screen backstack.

### Memory & Image Prefetching
- `HomeScreen` executes a pre-fetch coroutine (`imageLoader.enqueue()`) upon receiving driver standings to pre-cache driver headshot bitmaps before users navigate to detailed profile screens.

### Cold Start Latency Mitigation
- Free-tier Render backends experience 30–50 second cold starts. Room Database local cache rendering ensures immediate full schedule and standings display without blocking UI rendering.

---

## 9. Tech Debt & Future Architectural Roadmap

1. **Database Migrations**: Replace `.fallbackToDestructiveMigration(true)` with explicit incremental Room Migration scripts (`Migration(51, 52)`).
2. **Metadata API Endpoints**: Move hardcoded driver biographies and team principal information in `ProfileUtils.kt` to backend REST endpoints.
3. **Home Screen Widgets**: Implement Jetpack Compose Glance widgets for live session countdowns.
4. **WebSocket / SSE Live Streams**: Upgrade HTTP polling for team radio/session results to WebSockets or Server-Sent Events (SSE) for real-time race weekend sector timing.
