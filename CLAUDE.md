# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android Week View is a Kotlin Android library for displaying weekly schedules/timetables using Jetpack Compose. The legacy View-based implementation was removed in 3.0.0; the library is now Compose-only.

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
./gradlew library:testDebugUnitTest --tests "de.tobiasschuerg.weekview.data.WeekDataTest"  # Single test class
./gradlew ktlintCheck          # Verify code style
./gradlew ktlintFormat         # Auto-format code
./gradlew assembleDebug        # Build debug APK (sample app)
```

Dependencies are managed via version catalog in `gradle/libs.versions.toml`.

Always run `./gradlew ktlintFormat` before committing. Use conventional commit messages (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).

## Architecture

### Data Layer (`data/`)
- **Event** — sealed class: `Event.Single` (timed), `Event.AllDay`, `Event.MultiDay`. Only `Single` has full view rendering.
- **TimeSpan** — start/end `LocalTime` pair with lazy `Duration`. Factory: `TimeSpan.of(startTime, duration)`.
- **LocalDateRange** — iterable `ClosedRange<LocalDate>` used by `WeekData` to define the visible date range.
- **WeekData** — event container that auto-expands its time span as events are added. Validates events fall within its `LocalDateRange`.
- **EventConfig** / **WeekViewConfig** — display configuration data classes.

### Utility Layer (`util/`)
- **EventOverlapCalculator** — BFS graph algorithm to find connected components of overlapping events, returns layout fractions (widthFraction, offsetFraction).
- **EventPositionUtil** — calculates vertical offset and height based on scaling factor and visible time range.
- **DayOfWeekUtil** — locale-aware day-of-week to column index mapping.

### Compose Layer (`compose/`)
- **WeekViewCompose** — main entry composable. Takes `WeekData`, config objects, and `WeekViewActions` callbacks.
- **WeekBackgroundCompose** — grid rendering with day headers, time axis, current-time indicator.
- **EventCompose** / **EventsWithOverlapHandling** — event card rendering with overlap layout.
- **`components/`** — `GridCanvas`, `DayHeaderRow`, `TimeAxisColumn`, `EventsPane`, `AllDayEventsRow`, `MultiDayEventsRow`.
- **`state/`** — `WeekViewMetrics` for layout calculations, `RememberWeekViewMetrics` for Compose state integration.
- **`style/`** — `WeekViewStyle` theming.
- **WeekViewGesture** — pinch-zoom and swipe gesture handling.

## CI/CD and Releases

**GitHub Actions workflows** (`.github/workflows/`):
- **`pr.yml`** — runs on PRs: lint (`lintDebug`), unit tests (`testDebugUnitTest`), debug build. Auto-merges Dependabot patch/minor PRs.
- **`build.yml`** — runs on push to `master`/`develop`: same quality checks + build.
- **`release.yml`** — triggers after a successful `build.yml` on `master`. Runs `scripts/determine-version.sh` to auto-bump the version based on conventional commits (`feat:` → minor, `fix:` → patch, `!`/`BREAKING CHANGE` → major), creates a git tag (bare `X.Y.Z`, no `v` prefix), and publishes a GitHub release. JitPack picks up the tag automatically.

Library version is derived at build time from the latest git tag (`git describe --tags` in root `build.gradle.kts`).

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

Use the sample app (`app/` module) for manual integration testing.
