# PitWall — Design System & UI Guidelines

This document specifies the complete design system, visual rules, color tokens, typography scales, layout architectures, component guidelines, visual asset rules, and responsiveness standards for the **PitWall** Android application (`com.jenil.f1comp`).

---

## 1. Brand Identity & Design Philosophy

### Brand Pillars
- **Name**: PitWall
- **Tagline**: *"Built for the Obsessed."*
- **Aesthetic Core**: High-tech telemetry dashboard, telemetry monitor aesthetic, crisp contrast, dark-first motorsport interface.
- **Visual Personality**: Precise, tactical, aggressive yet refined, readable at a glance during high-speed live sessions.

### Core Principles
1. **Telemetry First**: Important numerical data (lap times, gaps, points, positions, countdowns, sector splits) uses monospaced typography and high contrast for immediate legibility.
2. **Livery Authenticity**: Constructor teams are represented by their true 2026 livery colors and official team branding badges.
3. **Glass & Depth**: Subtle glassmorphism (`Haze` library), thin stroke outlines (`1.dp`), and controlled surface elevation (`0.dp` cards with custom border strokes) keep the UI lightweight and modern.
4. **Pill & Squircle Form Factors**: Smooth rounded corners (`16.dp` for cards, `20.dp` for hero components, `25` / `50` for pills and interactive toggles).

---

## 2. Color System & Design Tokens

### Core Brand & Theme Colors

| Token Name | Hex Code | Purpose / Context |
| :--- | :--- | :--- |
| `F1Red` | `#E10600` | Primary brand accent, default active state, racing highlight. |
| `LightBackground` | `#F5F3EE` | Light theme background (warm off-white). |
| `LightSurface` | `#FFFFFF` | Light theme elevated card & sheet surface. |
| `LightTextPrimary` | `#15151E` | Light theme primary text & high-contrast titles. |
| `LightTextSecondary` | `#757575` | Light theme body text, labels, and muted subtitles. |
| `LightSurfaceVariant` | `#E8E6E1` | Light theme inner containers, dividers, and inactive pills. |
| `LightOutline` | `#D6D3CD` | Light theme default borders & stroke outlines. |
| `LightError` | `#B00020` | Light theme error states & warning alerts. |
| `DarkBackground` | `#0F0F0F` | Dark theme deep black background. |
| `DarkSurface` | `#15151E` | Dark theme surface (dark blue-grey). |
| `DarkTextPrimary` | `#F5F3EE` | Dark theme primary text. |
| `DarkTextSecondary` | `#9E9E9E` | Dark theme secondary text & telemetry labels. |
| `DarkSurfaceVariant` | `#1E1E2A` | Dark theme elevated container variant. |
| `DarkOutline` | `#2C2C3A` | Dark theme card outlines & subtle borders. |
| `DarkError` | `#CF6679` | Dark theme error highlights. |

---

### Preset Theme Primary Swatches

Users can customize the app's primary accent color via Theme Settings. Each swatch dynamically adjusts system color schemes and active states:

| Theme Swatch ID | Label | Primary Hex | Character / Mood |
| :--- | :--- | :--- | :--- |
| `classic_red` | Classic Red | `#E10600` | Signature Formula 1 Racing Red (Default) |
| `midnight_teal` | Midnight Teal | `#004D40` | Deep Technical Teal |
| `paddock_orange` | Amber | `#FF9800` | High-visibility Paddock Orange |
| `racing_blue` | Racing Blue | `#1976D2` | Electric Speed & Precision Blue |

---

### 2026 Constructor Team Livery Colors

Every F1 constructor team features a dedicated primary livery color token, subtitle descriptor, and primary hex code:

| Constructor Team ID | Official Team Name | Primary Hex | Subtitle / Livery Palette |
| :--- | :--- | :--- | :--- |
| `team_ferrari` | Scuderia Ferrari HP | `#E8002D` | Rosso Corsa • Giallo Modena |
| `team_redbull` | Oracle Red Bull Racing | `#3671C6` | Matte Navy • Crimson Accent |
| `team_mercedes` | Mercedes-AMG PETRONAS F1 Team | `#27F4D2` | Petronas Teal • Obsidian |
| `team_mclaren` | McLaren Mastercard F1 Team | `#FF8000` | Papaya Orange • Anthracite |
| `team_astonmartin` | Aston Martin Aramco F1 Team | `#229971` | Racing Green • Lime |
| `team_alpine` | BWT Alpine F1 Team | `#00A1E8` | French Blue • BWT Pink |
| `team_williams` | Atlassian Williams F1 Team | `#1868DB` | Heritage Blue • Navy |
| `team_rb` | Visa Cash App Racing Bulls | `#6692FF` | Royal Blue • White |
| `team_sauber` | Audi Revolut F1 Team | `#FF2D00` | Titanium Silver • Audi Red |
| `team_haas` | TGR Haas F1 Team | `#DEE1E2` | Racing Red • Carbon Black |
| `team_cadillac` | Cadillac F1 Team | `#AAAAAD` | Carbon Black • Metallic Gold |

---

### Podiums & Standings Colors

| Token Name | Hex Code | Usage |
| :--- | :--- | :--- |
| `PodiumGold` | `#FFD700` | P1 Gold Medal Badge & Championship Leader Tertiary Accent. |
| `PodiumSilver` | `#B0BEC5` | P2 Silver Badge & Secondary Standings Accent. |
| `PodiumBronze` | `#8D6E63` | P3 Bronze Badge & Tertiary Standings Container. |
| `P1Text` | `#8C5A00` | Contrast text on Gold surface. |
| `P2Text` | `#424242` | Contrast text on Silver surface. |
| `P3Text` | `#703D24` | Contrast text on Bronze surface. |

---

### Telemetry & Delta Colors

| Token Name | Hex Code | Usage |
| :--- | :--- | :--- |
| `FatestLap` | `#B15EFF` | Purple highlight for official session Fastest Lap times. |
| `DeltaGain` | `#2E7D32` | Green badge/text for grid positions gained (+N). |
| `DeltaLoss` | `#C62828` | Red badge/text for grid positions lost (-N). |
| Live Green Pulse | `#00D26A` / `#10B981` | Blinking status indicator for live sessions and connected status. |

---

## 3. Typography & Font System

PitWall systematically uses **`FontFamily.Monospace`** across titles, headers, telemetry labels, countdowns, driver codes, sector splits, and statistics to maintain the high-tech racing telemetry look.

```
Typography Hierarchy
├── headlineLarge   (32.sp, Bold, Monospace)       → Screen titles & primary statistics
├── titleLarge      (22.sp, SemiBold, Monospace)   → Screen headers & card titles
├── titleMedium     (18.sp, Medium, Monospace)     → Component section headers & driver names
├── bodyMedium      (14.sp, Normal, Monospace)     → Standard content text & circuit details
└── labelSmall      (11.sp, Medium, Monospace)     → Telemetry labels & upper-case tags
```

### Type Scale Specification

| Token Name | Font Family | Size (`sp`) | Font Weight | Letter Spacing | Usage Context |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `headlineLarge` | `FontFamily.Monospace` | `32.sp` | `FontWeight.Bold` | `0.sp` | Main numbers, countdown digits. |
| `titleLarge` | `FontFamily.Monospace` | `22.sp` | `FontWeight.SemiBold` | `0.sp` | Top bar title, Next Race title. |
| `titleMedium` | `FontFamily.Monospace` | `18.sp` | `FontWeight.Medium` | `0.15.sp` | Card titles, leader driver names. |
| `bodyMedium` | `FontFamily.Monospace` | `14.sp` | `FontWeight.Normal` | `0.25.sp` | Body text, circuit locations, list items. |
| `labelSmall` | `FontFamily.Monospace` | `11.sp` | `FontWeight.Medium` | `1.5.sp` | Upper-case section tags, metadata labels. |
| *Micro Tag* | `FontFamily.Monospace` | `10.sp` | `FontWeight.Bold` | `0.5.sp` | Pill button text, status badges, gap PTS. |
| *Nano Tag* | `FontFamily.Monospace` | `9.sp` | `FontWeight.Bold` | `0.sp` | Circuit type badge lines, selected labels. |

---

## 4. Spacing, Insets & Padding Tokens

### Screen-Level Padding Tokens (`F1ScreenPadding`)

System bar insets are dynamically calculated to ensure edge-to-edge support across all screen form factors:

```kotlin
object F1ScreenPadding {
    @Composable
    fun topPadding(extra: Dp = 8.dp): Dp =
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + extra

    @Composable
    fun bottomPadding(extra: Dp = 72.dp): Dp =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + extra
}
```

### Spacing Token Scale

| Token Size | Value (`dp`) | Application Guidelines |
| :--- | :--- | :--- |
| `Micro` | `2.dp` – `4.dp` | Inner text-to-badge gaps, divider line margins, dot margins. |
| `Compact` | `6.dp` – `8.dp` | Icon-to-text spacing, row item gaps, small chip padding. |
| `Standard` | `10.dp` – `12.dp` | Item gaps in vertical lazy columns, inner card vertical padding. |
| `Medium` | `14.dp` – `16.dp` | Horizontal screen margins, default card padding (`16.dp`). |
| `Large` | `20.dp` – `24.dp` | Hero card inner padding, drawer header horizontal padding. |
| `Section` | `28.dp` – `32.dp` | Gap between major theme sections or full-screen scroll bottoms. |

---

## 5. Shape Tokens, Elevation & Border System

### Corner Radius Scale

| Shape Token | Radius Spec | Components Applied To |
| :--- | :--- | :--- |
| `CardShape` | `RoundedCornerShape(16.dp)` | Standard cards (`NextRaceCard`, `ScheduleCard`, `NewsCard`, `RadioCard`, `SettingsItem`, `TimingTowerCard`). |
| `HeroCardShape` | `RoundedCornerShape(20.dp)` | `PodiumHeroCard`, `LeaderCard`, `UserProfileHero`, executive theme toggles. |
| `SheetShape` | `RoundedCornerShape(12.dp)` | `PitwallDrawer` header containers, weather badges, status banners. |
| `BadgeShape` | `RoundedCornerShape(8.dp)` | `CircuitTypeBadge`, position gap pills, session type tags, tyre compound chips. |
| `PillShape` | `RoundedCornerShape(25)` / `(28.dp)` / `(50)` | `F1BottomNavigationBar`, `F1NavigationRail`, `PillButton`, `StatusPill`, `StandingsToggle`. |
| `CircleShape` | `CircleShape` (100%) | Avatars, team logo circles, avatar halo rings, gantry lights, theme swatches, radio selection dots. |

### Border Strokes & Outlines
- **Default Card Border**: `BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)` or `outlineVariant.copy(alpha = 0.4f)`.
- **Active / Next Race Border**: `BorderStroke(1.dp, MaterialTheme.colorScheme.primary)` or `1.5.dp` team color (`teamColor.copy(alpha = 0.5f)`).
- **Validation Error Border**: `BorderStroke(1.5.dp, F1Red)` on text fields during failed input validation.
- **Hero Card Border**: `BorderStroke(1.5.dp, teamColor.copy(alpha = 0.5f))`.
- **Elevation Policy**: Flat zero elevation (`0.dp`) throughout; depth is established strictly through high-contrast surface color differences (`surface` vs. `surfaceVariant`) and precision border strokes.

---

## 6. Glassmorphism & Materials

PitWall leverages **Haze Materials** for floating navigation surfaces:

- **Haze Source**: Attached to the root `NavHost` via `.hazeSource(state = hazeState)`.
- **Haze Effect on Bottom Navigation**: Applied to `F1BottomNavigationBar` via `.hazeEffect(state = hazeState, style = HazeMaterials.thin(containerColor = MaterialTheme.colorScheme.surfaceVariant))`.
- **Haze Effect on Navigation Rail**: Applied to `F1NavigationRail` for wide/landscape viewports.
- **Selected Navigation Pill**: `MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)` backdrop with smooth scale (`1.1f`) and color transitions (`250ms` tween).

---

## 7. Component Library & Visual Specifications

### 1. Navigation Components

#### Floating Bottom Navigation Bar (`F1BottomNavigationBar`)
- **Container**: Floating pill with `RoundedCornerShape(28.dp)`, 20.dp horizontal screen inset, 12.dp vertical bottom padding.
- **Background**: Translucent surface with thin haze blur effect.
- **Items**: Home, Standings, Schedule, News.
- **Active State**: Selected icon scales to `1.1f`, background pill expands with `0.14f` primary color tint, text weight becomes `FontWeight.Bold`, haptic feedback fires.

#### Adaptive Side Navigation Rail (`F1NavigationRail`)
- **Container**: Vertical side rail with translucent haze surface. Automatically activates on wide width windows or landscape device orientation.
- **Items**: Home, Standings, Schedule, News, plus quick drawer toggle trigger.

#### Navigation Drawer (`PitwallDrawer`)
- **Width Bound**: Responsive max width constraint `widthIn(max = (screenWidth - 56.dp).coerceAtMost(360.dp))`.
- **Header**: Pinned branding section featuring `PitWall` app icon, title, *"Built for the Obsessed."* subtitle, and Next Race live countdown capsule.
- **Content Sections (Scrollable)**:
  1. `RACE HUB`: Radio Transmissions, Results, Apex AI, Telemetry.
  2. `UTILITIES`: Sync Calendar, Share App.
  3. `PREFERENCES`: Paddock Profile, Settings, Data Attribution.
- **Footer**: Connected green status dot, app version string (`v1.2.2`), feedback trigger, and Executive Segmented Light/Dark Theme Switch.

---

### 2. Cloud Auth & Security Components

#### Password Security Spectrum Bar
- **Container**: 5-segment horizontal bar displaying real-time password strength.
- **Criteria Evaluated**: 8+ Characters, Uppercase Letter (A-Z), Lowercase Letter (a-z), Number (0-9), Special Character (!@#$%^&*).
- **Color Transitions**:
  - `0–1 criteria`: Deep Red (`#E10600`)
  - `2–3 criteria`: Amber / Orange (`#FF9800`)
  - `4 criteria`: Yellow-Green (`#8BC34A`)
  - `5 criteria`: Speed Green (`#2E7D32`)
- **Requirement Chips**: Row of 5 pill tags with checkmark icons that fill with green background when satisfied.

#### Targeted Error Highlighting & Telemetry Snackbars
- **OutlinedTextField State**: Error states highlight target text field borders with `1.5.dp` `F1Red` stroke.
- **Telemetry Snackbar**: Top-anchored snackbar alert displaying high-tech telemetry message string.

---

### 3. Redesigned Paddock User Profile Hub (`UserProfileScreen`)

- **Profile Hero Card**:
  - Avatar Halo Ring: Outer circular ring tinted with user's favorite team primary color.
  - Photo Picker Button: Camera badge overlay triggering Android system gallery photo picker.
  - Verified Driver Badge: Checkmark badge displaying user callsign and email.
- **Interactive Dropdown Selectors**:
  - Favorite Constructor dropdown with team logos and official team swatches.
  - Favorite Driver dropdown with driver headshots and racing numbers.
- **Community & Support Action Chips**:
  - Direct shortcuts for Email Support, WhatsApp Paddock Community, Play Store Reviews, and Socials.
- **Telemetry Footer Tag**: Pinned bottom label displaying *"Built for the fans with ❤️ in India 🇮🇳"*.

---

### 4. Telemetry & Timing Tower Components

#### Timing Tower Row (`TimingTowerRow`)
- **Position Box**: Position number container (`P1`, `P2`, `P3` in podium colors, `P4+` in surfaceVariant).
- **Driver Identifier**: Driver code (e.g., `VER`, `NOR`, `LEC`), team color indicator line, driver full name.
- **Gap / Interval Column**: Interval gap to leader or car ahead in seconds (`+1.234s`, `LEADER`).
- **Sector Split Indicators**: 3 sector boxes (`S1`, `S2`, `S3`) showing purple for session best, green for personal best, yellow for slower.
- **Tyre Compound Badge**: Colored tyre icon chip (`S` Red for Soft, `M` Yellow for Medium, `H` White for Hard, `I` Green for Intermediate, `W` Blue for Wet).

#### Race Control Cards (`RaceControlCard`)
- **Container**: `RoundedCornerShape(12.dp)` card with category-colored left border stroke (Yellow for Safety Car / Flags, Green for Track Clear, Red for Red Flag).
- **Timestamp & Message**: Elapsed session time label with uppercase race control message text.

---

### 5. Next Race & Telemetry Cards

#### Next Race Hero Card (`NextRaceCard`)
- **Structure**:
  - Header: Round tag + Live session pill (`LiveSessionPill`) or Sprint tag (`SprintPill`).
  - Title: Grand Prix Name (`titleLarge`) + Country Flag (`24.dp` x `18.dp`).
  - Circuit Info: Circuit name & country location.
  - Gantry Lights: Animated `LightsOutCard` with 5 gantry lights lighting up sequentially every 1 second.
  - Countdown Grid: Big Day progress card (`DayCard`) paired with Hours/Minutes/Seconds countdown boxes (`SmallCountdown`) and dynamic `WeatherBadge`.
  - Timeline: Interactive `SessionTimelineBar` showing FP1, FP2, FP3, Quali, Sprint, and Race completion statuses.

#### Lights Out Gantry (`LightsOutCard`)
- **Container**: Dark pill (`#121212`) with `1.dp` border (`White` `0.08f` alpha).
- **Light Bezel**: `16.dp` outer circle with `10.4.dp` inner bulb (`#260A0C` unlit, `#E10600` lit).
- **Pulse Dot**: `8.dp` green dot (`#00D26A`) with continuous infinite scale animation (`1f` → `2.5f`) and alpha decay (`1f` → `0f`).

---

### 6. Standings & Hero Cards

#### Podium Hero Card (`PodiumHeroCard`)
- **Podium Row**: P2 (Left, Silver), P1 (Center, Gold, elevated), P3 (Right, Bronze).
- **Driver Avatar**: `AvatarCircle` with team color border ring, position badge (`1`, `2`, `3`), and driver headshot.
- **Race Winner Badge**: Detailed driver name, constructor team name, and race time / point total.

#### Standings Overview (`TopStandingsCard` & `Standing3Card` / `Driver3Card`)
- **Toggle Control**: `StandingsToggle` switching between "DRIVERS (WDC)" and "CONSTRUCTORS (WCC)".
- **Leader Card (`LeaderCard` / `LeaderDriverCard`)**: Full width card with `20.dp` corners and `1.5.dp` team color border.
- **Contender Cards (`ContenderCard` / `ContenderDriverCard`)**: 2-column split row (`weight(1f)` each) for P2 and P3.

---

### 7. Interactive Controls & Buttons

#### Segmented Pill Switch (`PillButton`)
- **Shape**: `RoundedCornerShape(25)`.
- **Animation**: Sliding background pill indicator animated with spring physics (`dampingRatio = 0.75f`, `stiffness = MediumLow`).
- **Indicator**: Active segment fills with `F1Red` and displays a pulsing white dot.

#### Settings Row (`SettingsItem` & `SettingsSwitchItem`)
- **Row Height**: `16.dp` padding vertical.
- **Elements**: Leading Material Icon (`24.dp`), Title (`bodyLarge`, `Medium`), Optional Subtitle (`bodySmall`, `onSurfaceVariant`), Trailing Chevron or Material 3 `Switch`.

---

### 8. Content Cards

#### Schedule Card (`ScheduleCard`)
- **Header**: Flag image, Grand Prix name, circuit location, and `CircuitTypeBadge` ("STREET CIRCUIT" / "PERMANENT CIRCUIT").
- **Completed State**: "Done" pill, round number, formatted race date, and "Results →" outlined button.
- **Upcoming State**: Expandable layout with Laps, Circuit Length, Layout SVG preview, and 2-column grid of session times (FP1, FP2, FP3, Quali, Sprint).

#### News Card (`NewsCard`)
- **Hero Card (First Item)**: Large featured article image with gradient overlay, headline text, relative timestamp (`2h ago`), and source domain.
- **Standard Cards**: Compact row with article thumbnail (`80.dp` x `80.dp`), title (`2` max lines), and relative date.

---

## 8. Asset & Icon Rules

### Icon System
1. **Material Icons**: Standard Material 3 icons (`Icons.Outlined.*`, `Icons.Rounded.*`, `Icons.AutoMirrored.*`).
2. **Custom Vector Resources**:
   - `calendar_add_on`: Custom calendar sync vector icon.
   - `ic_home`, `ic_news`, `ic_bar`, `ic_cal`, `ic_menu`: Custom navigation bar vector icons.
   - `apexMark`: Custom AI assistant brand vector icon.

### Flags & Country Badges
- **Flag CDN Engine**: Dynamic URL resolution via `https://flagcdn.com/w80/{code}.png`.
- **Country Code Resolver**: `ProfileUtils.getCountryCode(nationality)` and `ProfileUtils.emojiToCountryCode(emoji)` map nationalities and Unicode flag emojis to ISO 2-letter country codes with `"un"` (United Nations) fallback.
- **Rendering**: Displayed via `FlagImage` composable with `AsyncImage`, clip shape `RoundedCornerShape(2.dp)` or `4.dp`, and subtle border outline.

### Driver Headshots & Team Logos
- **Resolution Hierarchy**:
  1. Network API Entity URLs (`DriverProfileEntity.headshotUrl`, `ConstructorProfileEntity.logo`).
  2. Static Fallback CDN Mapping (`TeamUtils.getDriverImageUrl()`, `TeamUtils.getTeamLogoUrl()`).
  3. Local Drawable Resources (`TeamUtils.getDriverDrawableRes()`, `TeamUtils.getTeamLogoDrawableRes()`).
- **Placeholder Engine**: Circle avatar with driver/team initials when images are unavailable.

---

## 9. Responsiveness, Adaptive Rules & Window Insets

### Edge-to-Edge Strategy
- MainActivity executes `enableEdgeToEdge()` upon launch.
- Status bar and navigation bar colors are controlled dynamically via `WindowCompat.getInsetsController()`:
  ```kotlin
  isAppearanceLightStatusBars = !darkTheme
  isAppearanceLightNavigationBars = !darkTheme
  ```

### Screen & Container Bounds
- **Adaptive Layout**: Standard mobile displays present `F1BottomNavigationBar`. Tablet and landscape viewports display `F1NavigationRail`.
- **Drawer Bounds**: Navigation drawer enforces `.widthIn(max = (screenWidth - 56.dp).coerceAtMost(360.dp))` to prevent over-stretching on wide tablets or foldables.
- **Card Padding**: Horizontal screen padding is clamped at `16.dp` on phone form factors.
- **Scroll Containment**: Content columns utilize `.verticalScroll(scrollState)` with bottom padding set to `F1ScreenPadding.bottomPadding()` to avoid bottom bar overlap.

---

## 10. Summary Checklist for Developers

- [x] Always use `FontFamily.Monospace` for numerical values, driver codes, telemetry, and titles.
- [x] Use `F1ScreenPadding.topPadding()` and `F1ScreenPadding.bottomPadding()` for top and bottom screen container paddings.
- [x] Never hardcode constructor colors — always resolve via `TeamUtils.getTeamColor(constructorName)`.
- [x] Keep cards flat (`0.dp` elevation) and rely on `1.dp` borders (`surfaceVariant` or `outline`) for separation.
- [x] Maintain `16.dp` horizontal edge padding across all primary screens.
- [x] Use `collectAsStateWithLifecycle()` when observing `StateFlow` streams in composables.
