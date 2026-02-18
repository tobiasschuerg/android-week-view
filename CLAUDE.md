# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android Week View is a Kotlin Android library for displaying weekly schedules/timetables. It has two implementations: a **legacy View-based** one (deprecated, `view/` package) and a **modern Jetpack Compose** one (recommended, `compose/` package). New features go in Compose only; the legacy View code receives bug fixes only.

- **Package**: `de.tobiasschuerg.weekview`
- **Modules**: `library/` (the published library) and `app/` (sample/demo app)
- **Distribution**: JitPack from GitHub tags
- **Min SDK**: 21, **Compile/Target SDK**: 36, **Java**: 17 toolchain
- **Kotlin**: 2.3.10, **AGP**: 9.0.1, **Gradle**: 9.3.1

## Build Commands

```bash
./gradlew clean build          # Full build
./gradlew library:build        # Library only
./gradlew test                 # Run unit tests (library/src/test/)
./gradlew library:testDebugUnitTest  # Library unit tests only
./gradlew ktlintCheck          # Verify code style
./gradlew ktlintFormat         # Auto-format code
./gradlew assembleDebug        # Build debug APK (sample app)
```

Always run `./gradlew ktlintFormat` before committing. Use conventional commit messages (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).

## Architecture

### Data Layer (`data/`)
- **Event** — sealed class: `Event.Single` (timed), `Event.AllDay`, `Event.MultiDay`. Only `Single` has full view rendering.
- **TimeSpan** — start/end `LocalTime` pair with lazy `Duration`. Factory: `TimeSpan.of(startTime, duration)`.
- **WeekData** — event container that auto-expands its time span as events are added. Validates events fall within its date range.
- **EventConfig** / **WeekViewConfig** — display configuration data classes.

### Utility Layer (`util/`)
- **EventOverlapCalculator** — BFS graph algorithm to find connected components of overlapping events, returns layout fractions (widthFraction, offsetFraction).
- **EventPositionUtil** — calculates vertical offset and height based on scaling factor and visible time range.
- **DayOfWeekUtil** — locale-aware day-of-week to column index mapping.

### Compose Layer (`compose/`)
- **WeekViewCompose** — main entry composable. Takes `WeekData`, config objects, and `WeekViewActions` callbacks.
- **WeekBackgroundCompose** — grid rendering with day headers, time axis, current-time indicator.
- **EventCompose** / **EventsWithOverlapHandling** — event card rendering with overlap layout.
- **`components/`** — `GridCanvas`, `DayHeaderRow`, `TimeAxisColumn`, `EventsPane`.
- **`state/`** — `WeekViewMetrics` for layout calculations.
- **`style/`** — `WeekViewStyle` theming.

### Legacy View Layer (`view/`) — Deprecated
- `WeekView` (RelativeLayout), `WeekBackgroundView`, `EventView`. XML attributes in `res/values/attr.xml`.

## Key Patterns

- `java.time` API everywhere (LocalDate, LocalTime, Duration) with core library desugaring for API < 26
- Sealed class hierarchy for type-safe event variants
- Stateless composables rendered from `WeekData` state
- `WeekViewActions` interface for loosely-coupled event callbacks (all nullable)
- Gesture support: pinch-zoom via `TransformableState`, swipe navigation, tap/long-press

## Testing

Unit tests in `library/src/test/java/de/tobiasschuerg/weekview/` using JUnit 4:
- `WeekDataTest` — event addition, time span expansion, date range validation
- `TimeSpanTest` — duration calculation, hourly time generation
- `EventPositionUtilTest` — vertical offset/height calculations
- `DayOfWeekUtilTest` — day-to-column mapping

Use the sample app (`app/` module) for manual integration testing of both implementations.
