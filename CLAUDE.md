# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android Week View is a Kotlin Android library for displaying weekly schedules/timetables using Jetpack Compose. The legacy View-based implementation was removed in 3.0.0; the library is now Compose-only.

- **Package**: `de.tobiasschuerg.weekview`
- **Modules**: `library/` (the published library) and `app/` (sample/demo app)
- **Distribution**: JitPack from GitHub tags
- **Min SDK**: 26, **Compile/Target SDK**: 36, **Java**: 17 toolchain
- Kotlin uses AGP 9.0 built-in Kotlin support (no separate `kotlin-android` plugin)
- Dependency versions are in `gradle/libs.versions.toml`

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

Always run `./gradlew ktlintFormat` before committing. Use conventional commit messages (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).

## Architecture

All library source is under `library/src/main/java/de/tobiasschuerg/weekview/` in three layers:

### Data Layer (`data/`)
- **Event** — sealed class: `Event.Single` (timed), `Event.AllDay`, `Event.MultiDay`
- **WeekData** — event container that auto-expands its visible time span as events are added; validates events fall within its `LocalDateRange`
- **TimeSpan** — start/end `LocalTime` pair with lazy `Duration`. Factory: `TimeSpan.of(startTime, duration)`
- **EventConfig** / **WeekViewConfig** — display configuration data classes

### Utility Layer (`util/`)
- **EventOverlapCalculator** — BFS graph algorithm to find connected components of overlapping events, returns layout fractions (widthFraction, offsetFraction)
- **EventPositionUtil** — vertical offset and height from scaling factor and visible time range
- **DayOfWeekUtil** — locale-aware day-of-week to column index mapping

### Compose Layer (`compose/`)
- **WeekViewCompose** — main entry composable; takes `WeekData`, config objects, and `WeekViewActions` callbacks
- **`components/`** — extracted composables: grid canvas, day headers, time axis, events pane, all-day/multi-day rows
- **`state/`** — `WeekViewMetrics` for layout calculations; `rememberWeekViewMetrics` for Compose state integration
- **`style/`** — `WeekViewStyle` theming with `WeekViewColors`
- **WeekViewGesture** — pinch-zoom via `TransformableState`, swipe navigation

## Key Patterns

- `java.time` API everywhere (LocalDate, LocalTime, Duration) — available natively with minSdk 26
- Sealed class hierarchy for type-safe event variants
- Stateless composables rendered from `WeekData` state
- `WeekViewActions` data class for loosely-coupled event callbacks (all nullable lambdas)

## CI/CD and Releases

GitHub Actions workflows (`.github/workflows/`):
- **`pr.yml`** — runs on PRs: lint, unit tests, debug build. Auto-merges Dependabot patch/minor PRs.
- **`build.yml`** — runs on push to `master`/`develop`: same quality checks + build.
- **`release.yml`** — triggers after successful `build.yml` on `master`. Runs `scripts/determine-version.sh` to auto-bump version based on conventional commits (`feat:` → minor, `fix:` → patch, `!`/`BREAKING CHANGE` → major). Updates `libVersion` in `gradle.properties`, creates a git tag (bare `X.Y.Z`, no `v` prefix), and publishes a GitHub Release. JitPack picks up the tag automatically.

Library version is defined in `gradle.properties` (`libVersion`) and read by the root `build.gradle.kts`.

## Testing

Unit tests in `library/src/test/` using JUnit 4:
- `WeekDataTest` — event addition, time span expansion, date range validation
- `TimeSpanTest` — duration calculation, hourly time generation
- `EventPositionUtilTest` — vertical offset/height calculations
- `DayOfWeekUtilTest` — day-to-column mapping

Use the sample app (`app/` module) for manual integration testing.
