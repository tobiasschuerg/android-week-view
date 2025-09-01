[![](https://jitpack.io/v/tobiasschuerg/android-week-view.svg)](https://jitpack.io/#tobiasschuerg/android-week-view)
[![Build Status](https://www.bitrise.io/app/6ba47c24369dd52a/status.svg?token=XyF0AXasZwgKuoub_tJUYA&branch=master)](https://www.bitrise.io/app/6ba47c24369dd52a)

# Android Week View

Modern Android library for displaying weekly schedules and events with both **Compose** and **Classic View** implementations.

Initially created for [Schedule Deluxe](https://play.google.com/store/apps/details?id=com.tobiasschuerg.stundenplan), this library provides a flexible week view component that's perfect for calendar apps, timetables, and schedule management.

<img alt="" src="https://github.com/tobiasschuerg/android-week-view/blob/master/meta/device-2018-02-24-121341.png" height="400">

## 🏗️ Implementation Options

### Option 1: Modern Compose Implementation

The modern, performant implementation built with Jetpack Compose.

#### Add to your Compose UI:

```kotlin
@Composable
fun MyWeekView() {
    val weekData = remember { WeekData() }
    val eventConfig = remember { EventConfig() }
    val weekViewConfig = remember { WeekViewConfig() }

    WeekViewCompose(
        weekData = weekData,
        eventConfig = eventConfig,
        weekViewConfig = weekViewConfig,
        onEventClick = { eventId ->
            // Handle event click
        }
    )
}
```

#### Create and add events:

```kotlin
val event = Event.Single(
    id = 1L,
    date = LocalDate.now(),
    title = "Team Meeting",
    shortTitle = "Meeting",
    timeSpan = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
    backgroundColor = Color.Blue,
    textColor = Color.White
)

weekData.add(event)
```

### Option 2: Legacy View Implementation (⚠️ Deprecated)

> **⚠️ Warning**: The View-based implementation is deprecated. Please migrate to the Compose version for better performance, modern UI patterns, and future support.

#### Add to your XML layout:

```xml

<de.tobiasschuerg.weekview.view.WeekView android:id="@+id/week_view" android:layout_width="match_parent" android:layout_height="wrap_content" app:accent_color="@color/colorAccent" app:start_hour="8" app:end_hour="18" />
```

#### Configure in your Activity:

```kotlin
// ⚠️ DEPRECATED - Use WeekViewCompose instead
val config = EventConfig(showSubtitle = false, showTimeEnd = false)
weekView.eventConfig = config
weekView.setShowNowIndicator(true)

weekView.setEventClickListener { eventView ->
    // Handle event click
}

val event = Event.Single(
    id = 1337,
    date = LocalDate.now(),
    title = "Dentist Appointment",
    shortTitle = "DENT",
    timeSpan = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
    backgroundColor = Color.RED,
    textColor = Color.WHITE
)
weekView.addEvent(event)
```

## 🎨 Customization

### Event Configuration

```kotlin
val eventConfig = EventConfig(
    showSubtitle = true,
    showTimeEnd = true
)
```

### Week View Configuration

```kotlin
val weekViewConfig = WeekViewConfig().apply {
    scalingFactor = 1.2f
}
```

### Available Event Types

```kotlin
// Single event with specific time (fully supported)
val meeting = Event.Single(
    id = 1L,
    date = LocalDate.of(2024, 1, 15),
    title = "Project Review",
    shortTitle = "Review",
    timeSpan = TimeSpan.of(LocalTime.of(14, 0), Duration.ofHours(2)),
    backgroundColor = Color.Blue,
    textColor = Color.White
)

// All-day event (data model available)
val holiday = Event.AllDay(
    id = 2L,
    date = LocalDate.of(2024, 1, 16),
    title = "National Holiday",
    shortTitle = "Holiday"
)

// Multi-day event (data model available)
val conference = Event.MultiDay(
    id = 3L,
    date = LocalDate.of(2024, 1, 20),
    title = "Tech Conference",
    shortTitle = "Conf",
    lastDate = LocalDate.of(2024, 1, 22)
)
```

> **Note**: Currently, only `Event.Single` is fully implemented in the view rendering. `Event.AllDay` and `Event.MultiDay` have data models but no view implementation yet.

## 📦 Installation

### Step 1: Add JitPack repository

Add this to your **root** `build.gradle` file:

```gradle
allprojects {
    repositories {
        // ... other repositories
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

Add this to your **app** `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.tobiasschuerg:android-week-view:2.0.0'
    
    // Required for Compose implementation
    implementation platform('androidx.compose:compose-bom:2025.08.01')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'
    
    // Required for java.time API support on older Android versions
    coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.1.5'
}
```

### Step 3: Enable desugaring (required)

In your **app** `build.gradle`:

```gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
        coreLibraryDesugaringEnabled true
    }
}
```

## 🔄 Migration Guide

### Migrating from View-based to Compose

**Before (Deprecated):**

```koltin
// XML Layout
<de.tobiasschuerg.weekview.view.WeekView android : id ="@+id/week_view" ... />

// Activity
weekView.addEvent(event)
weekView.setEventClickListener { ... }
```

**After (Recommended):**

```kotlin
// Compose
WeekViewCompose(
    weekData = weekData.apply { add(event) },
    onEventClick = { eventId -> TODO }
)
```

## 📱 Sample App

The included sample app demonstrates both implementations:

- **Modern Compose WeekView**
- **Legacy View-based WeekView**

Run the sample app to see all features in action and choose the implementation that fits your needs.

## 📋 API Documentation

### Migration Notes

Starting from version **2.0.0**:

- ✅ **Compose implementation** is the recommended approach
- ⚠️ **View-based implementation** is deprecated but maintained for compatibility

Starting from version **1.8.0**:

- Switched from ThreeTen Backport to core library desugaring
- Requires `coreLibraryDesugaring` to be enabled

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Run `./gradlew ktlintFormat` before committing
4. Add tests for new functionality
5. Update documentation
6. Submit a pull request

## 🔗 Links

- **JitPack**: https://jitpack.io/#tobiasschuerg/android-week-view
- **Sample App**: See `app/` module in this repository
- **Issues**: https://github.com/tobiasschuerg/android-week-view/issues

---

⭐ **Don't forget to star this repository if you find it useful!**
