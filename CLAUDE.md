# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android Week View is a Kotlin Android library for displaying weekly schedules/timetables using Jetpack Compose. The legacy View-based implementation was removed in 3.0.0; the library is now Compose-only.

- **Package**: `de.tobiasschuerg.weekview`
- **Modules**: `library/` (the published library), `app/` (sample/demo app) and `baselineprofile/` (drives the sample app to record the library's baseline profile, committed at `library/src/main/generated/baselineProfiles/` and shipped in the AAR)
- **Distribution**: JitPack from GitHub tags
- **Min SDK**: 26, **Compile/Target SDK**: 37, **Java**: 17 toolchain
- Kotlin uses AGP 9.0 built-in Kotlin support (no separate `kotlin-android` plugin)
- Dependency versions are in `gradle/libs.versions.toml`

## Build Commands

```bash
./gradlew clean build          # Full build
./gradlew library:build        # Library only
./gradlew test                 # Run unit tests (library/src/test/)
./gradlew library:testDebugUnitTest  # Library unit tests only
./gradlew library:testDebugUnitTest --tests "de.tobiasschuerg.weekview.model.WeekDataTest"  # Single test class
./gradlew ktlintCheck          # Verify code style
./gradlew ktlintFormat         # Auto-format code
./gradlew assembleDebug        # Build debug APK (sample app)
ANDROID_SERIAL=<emulator> ./gradlew :library:generateBaselineProfile  # Regenerate the shipped baseline profile (API 33+ device or emulator)
```

Always run `./gradlew ktlintFormat` before committing. One top-level class/object/enum per file. Use conventional commit messages (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).

## Architecture

All library source is under `library/src/main/java/de/tobiasschuerg/weekview/`. The package tells public API from implementation: everything under `internal/` has Kotlin `internal` visibility. The library builds in Kotlin explicit API mode, so every public declaration needs an explicit `public` modifier.

### Public API
- **Root package** — **WeekView** (main entry composable; takes `WeekData`, config objects, `WeekViewActions` callbacks, `WeekViewColors` and an optional `eventContent` slot; pinch-zoom keeps the time under the fingers in place), **WeekViewState** / `rememberWeekViewState` (zoom level and scroll position, saveable; `initialTime`, `scrollToTime`, `animateScrollToTime`), **WeekViewActions**, **WeekViewConfig**, **EventConfig**
- **`model/`** — **Event** (sealed: `Event.Single` timed, `Event.AllDay`, `Event.MultiDay`), **WeekData** (immutable event container; its visible time span widens to fit the timed events; validates that events fall within its `LocalDateRange` and IDs are unique), **TimeSpan** (start/end `LocalTime`; factory `TimeSpan.of(startTime, duration)`), **LocalDateRange**
- **`content/`** — the `EventContent` slot that draws a timed entry, its `EventContentScope`, and the built-in styles **FilledEventContent** (default), **TintedEventContent**, **OutlinedEventContent**; they differ only in container and colors and share the internal `EventFields` for the auto-fitting text
- **`style/`** — `WeekViewColors`, with defaults from the Material theme via `WeekViewDefaults.colors()`

### Implementation (`internal/`)
- **WeekBackgroundCompose** — lays out headers, all-day/multi-day rows, time axis, grid, events and current-time overlay; **EventCompose** places a timed entry (offset, size, clicks, semantics) and delegates drawing to the `EventContent` slot
- **`components/`** — grid canvas, day headers, time axis (stacked `7:00`/`AM` labels and the current-time pill, sized from `TimeAxisDefaults`), current-time overlay drawn above the events, events pane, all-day/multi-day rows
- **`layout/`** — **EventOverlapCalculator** (BFS over overlapping events, returns width/offset fractions), **EventPositionUtil** (vertical offset and height), **EventFieldLayout** (which entry fields fit the entry height, using real line heights), `WeekViewMetrics` / `rememberWeekViewMetrics` and `GridTimeSpanFill`
- **`time/`** — locale-aware date and time formatting helpers

## Key Patterns

- `java.time` API everywhere (LocalDate, LocalTime, Duration) — available natively with minSdk 26
- Sealed class hierarchy for type-safe event variants
- Stateless composables rendered from an immutable `WeekData`
- `WeekViewActions` data class for loosely-coupled event callbacks (all nullable lambdas)

## CI/CD and Releases

GitHub Actions workflows (`.github/workflows/`):
- **`pr.yml`** — runs on PRs: lint, unit tests, debug build. Auto-merges Dependabot patch/minor PRs.
- **`build.yml`** — runs on push to `master`/`develop`: same quality checks + build.
- **`release.yml`** — triggers after successful `build.yml` on `master`. Runs `scripts/determine-version.sh` to auto-bump version based on conventional commits (`feat:` → minor, `fix:` → patch, `!`/`BREAKING CHANGE` → major). Updates `libVersion` in `gradle.properties`, creates a git tag (bare `X.Y.Z`, no `v` prefix), and publishes a GitHub Release. JitPack picks up the tag automatically.

Library version is defined in `gradle.properties` (`libVersion`) and read by the root `build.gradle.kts`.

## Testing

Unit tests in `library/src/test/` using JUnit 5 (Jupiter):
- `WeekDataTest` — splitting events by type, time span widening, date range and unique ID validation
- `TimeSpanTest` — duration calculation, hourly time generation
- `EventPositionUtilTest` — vertical offset/height calculations
- `EventFieldLayoutTest` — field visibility, time label mode and title height per available height
- `LocalDateExtTest` — date formatting/pattern helpers
- `LocalTimeExtTest` — time formatting/locale defaults, splitting axis labels into clock time and AM/PM
- `WeekViewConfigTest` — scaling factor validation
- `GridTimeSpanFillTest` — extending the visible time span in whole hours so the grid fills the viewport

Instrumented Compose UI tests in `library/src/androidTest/` (e.g. `EventContentSlotTest` for the custom event content slot) stay on JUnit 4 (`androidx.test`/`ui-test-junit4` have no JUnit 5 equivalent for on-device tests).

Use the sample app (`app/` module) for manual integration testing.
