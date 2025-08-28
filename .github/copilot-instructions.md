# GitHub Copilot Instructions for Android Week View

## Project Overview

This is an Android library project that provides a customizable week view for displaying events in a schedule/timetable format. The library is written in Kotlin and targets Android applications.

### Key Information
- **Library Name**: Android Week View
- **Package**: `de.tobiasschuerg.weekview`
- **Current Version**: 1.11.0
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Language**: Kotlin with Java 17 toolchain
- **Build System**: Gradle with Android Gradle Plugin 8.7.0
- **Kotlin Version**: 2.0.21

## Project Structure

```
android-week-view/
├── app/                          # Sample application demonstrating library usage
│   └── src/main/java/de/tobiasschuerg/weekview/sample/
│       ├── SampleActivity.kt     # Main demo activity
│       └── EventCreator.kt       # Helper for creating sample events
├── library/                      # Main library module
│   └── src/main/java/de/tobiasschuerg/weekview/
│       ├── data/                 # Data models and configuration
│       │   ├── Event.kt         # Event data classes (Single, AllDay, MultiDay)
│       │   ├── EventConfig.kt   # Event display configuration
│       │   ├── WeekData.kt      # Week data container
│       │   └── WeekViewConfig.kt # Persistent view configuration
│       ├── util/                # Utility classes and extensions
│       │   ├── Animation.kt     # Animation utilities
│       │   ├── ContextUtil.kt   # Context-related utilities
│       │   ├── DayOfWeekUtil.kt # Day of week utilities
│       │   ├── LocalTimeExt.kt  # LocalTime extensions
│       │   ├── TextHelper.kt    # Text formatting utilities
│       │   ├── TimeSpan.kt      # Time span data class
│       │   └── ViewHelper.kt    # View-related utilities
│       └── view/                # Custom view components
│           ├── WeekView.kt      # Main week view component
│           ├── WeekBackgroundView.kt # Background grid view
│           └── EventView.kt     # Individual event view
└── meta/                        # Project metadata and screenshots
```

## Architecture and Design Patterns

### Core Components

1. **WeekView**: Main container view that orchestrates the week display
   - Extends `RelativeLayout`
   - Handles touch events, scaling, and event positioning
   - Manages event layout and user interactions

2. **EventView**: Individual event display component
   - Represents a single event in the week view
   - Supports context menus and click listeners
   - Includes scale animations for smooth appearance

3. **WeekBackgroundView**: Background grid component
   - Draws time slots and day columns
   - Provides visual structure for the week layout

### Data Models

- **Event**: Sealed class with three variants:
  - `Event.Single`: Regular timed events with start/end times
  - `Event.AllDay`: Full-day events
  - `Event.MultiDay`: Events spanning multiple days

- **TimeSpan**: Represents time intervals with start time and duration
- **EventConfig**: Configuration for event display appearance
- **WeekViewConfig**: Persistent configuration (e.g., zoom level)

## Key Technologies and Libraries

### Core Dependencies
- **Android SDK**: Target API 34, Min API 21
- **Kotlin**: 2.0.21 with coroutines support
- **AndroidX**: Annotation library 1.8.2
- **Core Library Desugaring**: For `java.time` API support on older Android versions

### Time Handling
- Uses `java.time` API (LocalDate, LocalTime, Duration)
- Supports desugaring for backwards compatibility
- No external time libraries (migrated from ThreeTen Backport)

### Build Tools
- **Gradle**: 8.7.0
- **KtLint**: Code formatting and linting (version 12.1.1)
- **JitPack**: Distribution platform for library releases

## Coding Standards and Conventions

### Kotlin Style Guide
- Follow official Kotlin coding conventions
- Use KtLint for automatic formatting
- Prefer data classes for immutable data
- Use sealed classes for type-safe hierarchies

### Naming Conventions
- **Classes**: PascalCase (e.g., `WeekView`, `EventConfig`)
- **Functions**: camelCase (e.g., `addEvent`, `setShowNowIndicator`)
- **Properties**: camelCase (e.g., `backgroundColor`, `timeSpan`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `PREFS_KEY_SCALING_FACTOR`)

### Code Organization
- Group related functionality in packages (`data`, `util`, `view`)
- Keep utility functions in dedicated utility classes
- Use extension functions for enhancing existing classes
- Prefer composition over inheritance

## Common Development Patterns

### Event Management
```kotlin
// Creating events
val event = Event.Single(
    id = 1L,
    date = LocalDate.now(),
    title = "Meeting",
    shortTitle = "MTG",
    timeSpan = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
    backgroundColor = Color.BLUE,
    textColor = Color.WHITE
)

// Adding to week view
weekView.addEvent(event)
```

### Configuration
```kotlin
// Event display configuration
val config = EventConfig(
    showSubtitle = false,
    showTimeEnd = false,
    useShortNames = true
)
weekView.eventConfig = config
```

### Event Listeners
```kotlin
// Click handling
weekView.setEventClickListener { eventView ->
    // Handle event click
}

// Context menu
weekView.setOnCreateContextMenuListener { menu, view, menuInfo ->
    // Create context menu
}
```

## Testing Guidelines

### Unit Tests
- Located in `library/src/test/java/`
- Test utility classes and data models
- Use JUnit 4 framework
- Example: `TimeSpanTest.kt`, `DayOfWeekUtilTest.kt`

### Integration Testing
- Use sample app for manual testing
- Test different event configurations
- Verify touch interactions and animations

## Build and Release Process

### Local Development
```bash
./gradlew clean build
./gradlew ktlintCheck  # Code style verification
./gradlew ktlintFormat # Auto-format code
```

### Publishing
- Uses JitPack for library distribution
- Version defined in root `build.gradle` as `lib_version`
- Automatic builds triggered by Git tags

## Performance Considerations

### View Optimization
- Reuse event views when possible
- Minimize layout passes during scrolling
- Use efficient drawing for background grid

### Memory Management
- Avoid memory leaks in event listeners
- Properly clean up views when removed
- Use appropriate data structures for event storage

## Migration and Compatibility

### API Changes
- Version 1.8.0+: Switched from ThreeTen Backport to desugaring
- Maintains backwards compatibility for existing APIs
- Uses `@RequiresApi` annotations where appropriate

### Android Version Support
- Minimum SDK 21 ensures wide device compatibility
- Core library desugaring enables modern time APIs on older devices
- Targets latest stable Android API (34)

## Common Issues and Solutions

### Time Zone Handling
- Library primarily works with local time concepts
- Consider time zone implications for multi-region apps
- Use `LocalDate` and `LocalTime` consistently

### Custom Event Types
- Extend `Event` sealed class for custom event types
- Implement proper data class conventions
- Ensure serialization compatibility if needed

### Performance with Many Events
- Consider event virtualization for large datasets
- Implement efficient event filtering by date range
- Optimize event overlap calculations

## Contributing Guidelines

### Code Quality
- Run KtLint before submitting PRs
- Add unit tests for new utility functions
- Update sample app to demonstrate new features
- Follow existing code patterns and architecture

### Documentation
- Update README.md for API changes
- Add KDoc comments for public APIs
- Include usage examples in documentation
- Update this Copilot instructions file for architectural changes

## IDE and Development Environment

### Recommended Setup
- **Android Studio**: Latest stable version
- **Kotlin Plugin**: Latest compatible version
- **KtLint Plugin**: For real-time code formatting
- **Git Integration**: For version control workflow

### Code Style Configuration
- Use KtLint configuration from root project
- Enable auto-format on save
- Configure import organization
- Set up code inspection profiles

This library focuses on providing a flexible, performant week view component for Android applications with emphasis on clean architecture, modern Kotlin practices, and extensive customization options.
