# PitWall (F1Comp) 🏁

PitWall is a modern, high-performance Formula 1 companion application for Android. It provides fans with real-time race information, comprehensive schedules, live standings, and the latest news from the world of F1, all wrapped in a sleek Material 3 interface.

---

## ✨ Features

- **🏎️ Real-time Home Dashboard**: 
    - **Next Race Countdown**: Stay ahead with a precise countdown to the next Grand Prix.
    - **Live Session Status**: Real-time tracking of ongoing sessions (FP1, FP2, Qualifying, Sprint, Race).
    - **Weather Integration**: Get track-side weather updates directly on your home screen.
    - **Recent Results**: Quick view of the podium from the most recent race.
    - **Standings Overview**: Instant access to the top drivers and constructors.
- **📅 Full Season Schedule**: Detailed breakdown of every Grand Prix weekend, including session times and circuit information.
- **🏆 Live Standings**: Complete Driver and Constructor championship tables with detailed points and positions.
- **📰 F1 News Hub**: Stay informed with the latest headlines and articles parsed directly from top F1 sources.
- **🛠️ Personalization**: Custom settings for theme (Light/Dark mode) and event alerts to ensure you never miss a green light.

---

## 🛠️ Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for a fully declarative and modern UI.
- **Architecture**: MVVM (Model-View-ViewModel) with the Repository pattern for clean separation of concerns.
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for robust and scalable DI.
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room) for offline caching and lightning-fast data retrieval.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for efficient API communication.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) for optimized image fetching and caching.
- **Data Parsing**: [Jsoup](https://jsoup.org/) for robust HTML scraping and news content extraction.
- **Testing**: [MockK](https://mockk.io/), [Turbine](https://github.com/cashapp/turbine), and [JUnit 4] for comprehensive unit and state flow testing.

---

## 🏗️ Project Structure

```text
app/src/main/java/com/jenil/f1comp/
├── data/
│   ├── local/        # Room Database, DAOs, and Entities (Offline Cache)
│   ├── model/        # Domain Data Models
│   ├── remote/       # Retrofit API Service definitions
│   └── repository/   # Single source of truth for data orchestration
├── di/               # Hilt Dependency Injection Modules
├── ui/
│   ├── components/   # Reusable Compose UI components
│   ├── navigation/   # App Navigation logic and Bottom Bar
│   ├── screen/       # Feature-specific screens (Home, Schedule, Standings, etc.)
│   ├── theme/        # Material 3 Theme definitions
│   └── state/        # UI State holders
├── viewmodel/        # Business logic and UI state management
└── util/             # Helper classes and Mappers
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 11+
- Android SDK 26+

### Setup
1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/F1Comp.git
   ```
2. **Open in Android Studio**:
   Import the project and let Gradle sync complete.
3. **Run the app**:
   Connect an Android device or start an emulator and click **Run**.

---

## 👨‍💻 Developer
Developed by **Jenil Macwan**.

---

## 📄 License
*Specify your license here (e.g., MIT, Apache 2.0)*
