<p align="center">
  <img src="https://i.ibb.co/cS2sxwzj/f1-companion-logo.png" alt="App Logo" width="140" height="140"/>
</p>

<h1 align="center">🏎️ PitWall — Your Modern Formula 1 Companion App</h1>

<p align="center">
  Live F1 data • Race schedules • Circuit weather • Clean Jetpack Compose UI
</p>

<p align="center">
  <a><img src="https://img.shields.io/badge/Platform-Android-brightgreen?style=for-the-badge" alt="Platform Android"></a>
  <a><img src="https://img.shields.io/badge/Language-Kotlin-0095D5?style=for-the-badge" alt="Kotlin"></a>
  <a><img src="https://img.shields.io/badge/UI-Jetpack%20Compose-6200EE?style=for-the-badge" alt="Jetpack Compose"></a>
  <a><img src="https://img.shields.io/badge/API-Jolpica-red?style=for-the-badge" alt="Jolpica API"></a>
  <a><img src="https://img.shields.io/badge/Weather-Open%20Meteo-blue?style=for-the-badge" alt="Open-Meteo"></a>
  <a><img src="https://img.shields.io/badge/Status-Under%20Development-yellow?style=for-the-badge" alt="Under Development"></a>
  <a><img src="https://img.shields.io/badge/License-MIT-lightgrey?style=for-the-badge" alt="License MIT"></a>
  <a><img src="https://img.shields.io/badge/Kotlin%20Version-2.3.0-blue?style=for-the-badge" alt="Kotlin Version"></a>
</p>

---

> 🚧 **Status: Active Development**  
> Some features are incomplete or may not work fully yet.

---

## 📸 Screenshots / UI Previews

| Home | Schedule | Standings |
|------|------------------|-------------|
| <img src="Screenshots/HomeScreen.jpeg" width="220" /> | <img src="Screenshots/Schedule Screen.png" width="220" /> | <img src="Screenshots/Standings Screen.jpeg" width="220" /> |

| Driver Info | Weather Screen |  
|-------------|----------------|
| <img src="Screenshots/Standings Screen-2.png" width="220" /> | <img src="Screenshots/Weather Screen.png" width="220" /> | 

---

## 🎯 Features

- 📅 Full race weekend schedule (FP1, FP2, FP3, Qualifying, Sprint, Race)  
- 🏎 Up-to-date News of the Paddock   
- 🏁 Circuit layout & detailed circuit information  
- 🌦 Weather forecast for circuit location using Open-Meteo  
- 🎨 Modern UI built with Jetpack Compose with custom theme of LIGHT / DARK MODE 
- ⚡ Clean architecture with Kotlin, Coroutines, and MVVM  

---

## 🧰 Tech Stack

| Layer       | Technology                          |
|-------------|--------------------------------------|
| Language    | [Kotlin](https://kotlinlang.org/) (2.3.0) |
| UI          | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| DI          | [Hilt](https://dagger.dev/hilt/) |
| Persistence | [Room](https://developer.android.com/training/data-storage/room) |
| Networking  | [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) |
| Image Loading| [Coil](https://coil-kt.github.io/coil/) |
| Web Scraping| [Jsoup](https://jsoup.org/) |
| State       | ViewModel + StateFlow / Coroutines   |
| APIs        | Jolpica (F1 data), Open-Meteo (weather) |
| Concurrency | Kotlin Coroutines                    |
| Architecture| MVVM + Clean modular separation      |

---

## 🚀 Getting Started

### 📌 Clone Repository  
```bash
git clone https://github.com/JenilMacwan/F1Companion.git
cd F1Companion
```

### ▶ Run the App
1. Open in **Android Studio**
2. Let Gradle resolve dependencies
3. Connect device/emulator
4. Press **Run ▶**

---

## 🔧 Current Limitations

- Settings screen not fully implemented 
- API data availability varies (New data gets updated after 24hrs)
- UI polishing ongoing
- Possible crashes during rapid data refresh

---

## 🗺 Roadmap

| Planned Feature | Status |
|----------------|--------|
| Improved animations & UI polish | 🔄 |
| Push notifications | ⏳ |
| Race history module | ⏳ |
| Favorite drivers & teams | ⏳ |
| Android Home Screen widget | ⏳ |
| Offline caching | ⏳ |

---

## 🤝 Contributing

```bash
git checkout -b feature-branch-name
git commit -m "Description of change"
git push origin feature-branch-name
```

Then open a **Pull Request**.

---

## ⭐ Support PitWall

If you like the project:
- Give the repo a ⭐ on GitHub
- Submit issues / feature requests
- Contribute via PRs

---

## 👨‍💻 Developer

| Name | Role |
|------|------|
| **JeniL (JenilMacwan)** | Developer & Project Owner |

---

## 📜 License

License will be added soon.  
Until then: **All Rights Reserved**

---

<p align="center">
  <strong>🏁 PitWall — Fast • Modern • Informative</strong><br>
  Built with Kotlin • Powered by APIs • Designed with Jetpack Compose
</p>
