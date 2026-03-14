[![](https://jitpack.io/v/tobiasschuerg/android-week-view.svg)](https://jitpack.io/#tobiasschuerg/android-week-view)
[![Build](https://github.com/tobiasschuerg/android-week-view/actions/workflows/build.yml/badge.svg)](https://github.com/tobiasschuerg/android-week-view/actions/workflows/build.yml)

# Android Week View

Kotlin Android library for displaying weekly schedules and timetables using Jetpack Compose.

Initially created for [Schedule Deluxe](https://play.google.com/store/apps/details?id=com.tobiasschuerg.stundenplan), this library provides a flexible week view component for calendar apps, timetables, and schedule management.

## Screenshots

| University | Work | School | Conference (3-day) |
|:---:|:---:|:---:|:---:|
| <img src="meta/preview_university.png" height="360"> | <img src="meta/preview_work.png" height="360"> | <img src="meta/preview_school.png" height="360"> | <img src="meta/preview_conference.png" height="360"> |

## Features

- Jetpack Compose implementation (Compose-only since 3.0)
- Three event types: timed, all-day, and multi-day (spanning bars)
- Automatic overlap handling for concurrent events
- Pinch-to-zoom
- Current time indicator and day highlighting
- Configurable event display, spacing, and time range
- Flexible day counts (3-day, 5-day, 7-day, etc.)
- Navigation handled externally for full control (e.g. `HorizontalPager`, buttons)

## Usage

### Add to your Compose UI

```kotlin
@Composable
fun MyWeekView() {
    val dateRange = LocalDateRange(
        LocalDate.now().with(DayOfWeek.MONDAY),
        LocalDate.now().with(DayOfWeek.FRIDAY),
    )
    val weekData = remember {
        WeekData(dateRange, LocalTime.of(8, 0), LocalTime.of(18, 0))
    }

    WeekViewCompose(
        weekData = weekData,
        weekViewConfig = WeekViewConfig(),
        eventConfig = EventConfig(),
        actions = WeekViewActions(
            onEventClick = { event -> /* Handle click */ },
            onEventLongPress = { event -> /* Handle long press */ },
        ),
    )
}
```

### Create events

```kotlin
// Timed event
val meeting = Event.Single(
    id = 1L,
    date = LocalDate.of(2026, 1, 15),
    title = "Team Meeting",
    shortTitle = "Meeting",
    timeSpan = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
    backgroundColor = Color.BLUE,
    textColor = Color.WHITE,
)

// All-day event
val holiday = Event.AllDay(
    id = 2L,
    date = LocalDate.of(2026, 1, 16),
    title = "National Holiday",
    shortTitle = "Holiday",
    backgroundColor = Color.GREEN,
    textColor = Color.WHITE,
)

// Multi-day event (renders as a spanning bar)
val conference = Event.MultiDay(
    id = 3L,
    date = LocalDate.of(2026, 1, 20),
    title = "Tech Conference",
    shortTitle = "Conf",
    lastDate = LocalDate.of(2026, 1, 22),
    backgroundColor = Color.MAGENTA,
    textColor = Color.WHITE,
)

weekData.add(meeting)
weekData.add(holiday)
weekData.add(conference)
```

## Customization

### Week View Configuration

```kotlin
val weekViewConfig = WeekViewConfig(
    scalingFactor = 1.2f,
    showCurrentTimeIndicator = true,
    highlightCurrentDay = true,
)
```

### Event Configuration

```kotlin
val eventConfig = EventConfig(
    showSubtitle = true,
    showTimeStart = true,
    showTimeEnd = true,
    eventSpacingDp = 1, // gap between adjacent events (0 to disable)
)
```

### Callbacks

```kotlin
val actions = WeekViewActions(
    onEventClick = { event -> /* Handle event tap */ },
    onEventLongPress = { event -> /* Handle long press */ },
    onScalingFactorChange = { factor -> /* Persist zoom level */ },
)
```

## Installation

### Step 1: Add JitPack repository

In your **settings.gradle.kts**:

```kotlin
dependencyResolutionManagement {
    repositories {
        // ... other repositories
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add the dependency

In your **app** `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.tobiasschuerg:android-week-view:4.0.0")

    // Required for Compose
    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
}
```

## Version History

**4.0.0** — Bumped minSdk to 26. Removed core library desugaring dependency.

**3.0.0** — Removed legacy View-based implementation. Compose only.

**2.0.0** — Added Compose implementation alongside deprecated View-based code.

**1.8.0** — Switched from ThreeTen Backport to core library desugaring.

## Sample App

The `app/` module contains a sample app with four built-in timetables (University, Work, School, Conference) demonstrating different layouts, overlapping events, all-day/multi-day events, and a 3-day view. Switch between them via the top app bar menu.

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Run `./gradlew ktlintFormat` before committing
4. Add tests for new functionality
5. Submit a pull request

## Links

- **JitPack**: https://jitpack.io/#tobiasschuerg/android-week-view
- **Sample App**: See `app/` module in this repository
- **Issues**: https://github.com/tobiasschuerg/android-week-view/issues
