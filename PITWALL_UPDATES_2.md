# PitWall Updates – 2

## Release separation

| Track | Scope | Version | Status |
|---|---|---|---|
| **Updates – 1** | Current fixes prepared for release, including OEM font issues, the Markdown parser, chatbot readiness, R8/DEX obfuscation, and the other fixes already in progress. | **1.0.1** | Planned closed-test release by Sunday, November 1, 2026. |
| **PitWall Updates – 2** | Future app-wide UI/UX redesign and live-session viewing improvements described below. | **TBD** | Planned; not part of Updates – 1. |

## Planned scope

1. **Design foundation** — Establish a cohesive visual system for colors, type, spacing, component shapes, icons, and interaction feedback. Improve readability, accessibility, and consistency across screen sizes.
2. **Navigation and home** — Make the app structure easier to understand, reduce friction to key tasks, and give the home screen a clear hierarchy for upcoming sessions, live sessions, and useful summaries.
3. **Live session experience** — Rework the live screen around session status, race context, and the timing tower using data the app already has. **Circuit map and live car coordinates are on hold** until a reliable, free live data source is found. Do not build or present a circuit map as part of the current Updates – 2 scope.
4. **Other screens** — Apply the same system to Schedule, Standings, Results, News, Radio, Apex, profile, settings, and account flows while preserving each screen’s primary task.
5. **Responsive behavior and polish** — Review loading, empty, error, and offline states; check touch targets and text scaling; and verify layouts across supported device sizes.
6. **Cross-device consistency** — Ensure every redesign and feature works and looks polished on both stock Android and OEM-modified Android devices. Account for differences in font rendering, spacing, system bars, display cutouts, image backgrounds, and device-specific UI behavior.

## Step 1: Current UI visual baseline

Reviewed the app running in the Android Studio Pixel 10a emulator (API 37.1), in portrait. This is a visual baseline for the running emulator build, not a Play Store build or an OEM-device comparison.

| Screen | What works | What to improve in Updates – 2 |
|---|---|---|
| **Home** | Next-race card gives the page a clear F1 identity; countdown, session timeline, and recent podium make useful information available immediately. | The page is information-dense and relies heavily on compact monospace text. Make the most relevant next/live-session action and supporting content easier to scan. |
| **Navigation drawer** | Destinations are grouped into Race Hub, Utilities, and Preferences; profile and favorites are discoverable. | Many destinations sit in one drawer, and the compact labels are difficult to scan. Revisit grouping, visual priority, and text sizing. |
| **Bottom navigation** | Four persistent destinations have both icons and labels, with a visible selected state; this is a clear foundation for common tasks. | Check label legibility, touch areas, and how the floating bar relates to scrollable content and system insets. |
| **Standings** | Driver/constructor switching is visible; team rows combine names and points in a familiar list. | Dense, small type makes the rows and values harder to scan quickly. Strengthen hierarchy between rank, name, and points. |
| **Schedule** | All/Upcoming/Completed filters and a featured next race make the schedule structure understandable. | The featured card and following race cards are information-heavy; improve grouping and legibility for date, location, and session details. |
| **News** | A prominent lead story image establishes a clear top story; article cards include useful actions. | The feed uses several compact text treatments. Improve title readability and keep lower content comfortably clear of the floating navigation. |

### Baseline-wide observations

- The warm light palette, racing-red accent, rounded surfaces, and motorsport telemetry character are recognizable and should inform the redesign.
- Monospace typography is applied broadly. Preserve it for timing and numerical data, then review a more readable text face for longer labels, news, and Apex explanations.
- The emulator review covered portrait on one stock-style device only. Landscape, larger windows, text scaling, and OEM behavior remain to be reviewed during implementation.
- Circuit map and live car coordinates remain on hold; this review does not add either to scope.

## Apex AI as a contextual race companion

Make Apex useful throughout the app, not only on its dedicated chat screen. Keep the chat experience, and add concise, screen-aware insights and clear ways to ask follow-up questions where users are already viewing F1 information.

### Planned experiences

- **Home weekend briefing** — Summarize the next session and relevant storylines, with optional personalization for the user's favorite driver or team.
- **Standings and results explanations** — Offer contextual actions such as explaining points changes, position changes, and teammate comparisons, with the relevant data visible alongside the explanation.
- **Weekend preparation** — Summarize circuit context, session times, and relevant news, with links to the underlying app screens and sources.
- **Live-session explanations** — Explain timing and race-control changes when that data is available. Circuit maps and live car positions are on hold until a reliable, free source is found.
- **Session recap** — Provide a concise, shareable summary based on session results and notable events.

### Rollout and trust

Begin with a Home weekend briefing, contextual actions on Standings and Results, and a sourced post-session recap. Use app data as the factual source, calculate straightforward comparisons from that data where possible, and use Apex to explain those facts in plain language. Show sources or relevant underlying data, identify incomplete or time-sensitive information, and keep race-control guidance attributed to its official source. Expand to live-session explanations as the required data becomes available. Circuit maps and live coordinates remain on hold.

## Delivery approach

- Audit and map existing screen flows before implementation.
- Define and review the design system and key screen concepts.
- Implement in focused screen groups, keeping navigation and shared components consistent.
- Validate usability and visual behavior on supported devices before selecting a release version.

## Scope guard

This document tracks the future redesign only. It does not change the current **1.0.1** version metadata, Updates – 1 release contents, or the existing release notes.

All Updates – 2 work should be checked on representative stock Android and OEM devices before release so the experience remains consistent across devices.
