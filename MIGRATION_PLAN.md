# Migration Compose WeekView

## Introduction

This migration plan documents the step-by-step process of converting the existing view-based Android
WeekView library to Jetpack Compose. The goal is to provide a modern, maintainable, and flexible
week view component for Android applications, supporting both the classic view-based implementation
and the new Compose-based approach during the transition. Each step is tracked for progress and
stability, with continuous rules to ensure code quality and maintainability throughout the
migration.

## Steps and Status

1. Analyze the existing view-based WeekView architecture [✔]
2. Create the Compose package and base structure [✔]
3. Implement the background grid in Compose [✔]
4. Integrate into the sample app with chooser for Classic and Compose [✔]
5. Refactor to use separate activities for Classic and Compose [✔]
6. Correct label positioning in the grid [✔]
7. Fix label positioning issues (boxSize, Dp conversion) [✔]
8. Clean up code quality issues and implement proper component separation [✔]
9. Fine-tune the UI (proper margins, label positioning) [✔]
10. Make time labels scroll vertically with the grid [✔]
11. Implement horizontal grid lines spanning full width including time column [✔]
12. Add dynamic now indicator with automatic time progression [✔]
13. Ensure scaling factor compatibility and proper alignment [✔]
14. Add current time display with minutes in time column [✔]
15. Implement event rendering in Compose [✔]
16. Fix event rendering issues [open]
    - Events do not scroll with the grid [✔] (Events and grid now share the same scrollable area and ScrollState. Modifier.matchParentSize ensures correct overlay and synchronized scrolling.)
    - Background grid ends before the last event
    - Event width is still too large [✔] (Event width is now reduced by a fixed margin, ensuring events are visually inside the column and do not overlap grid lines.)
    - Weekday titles need centering [✔] (Weekday titles are now horizontally and vertically centered using Modifier.fillMaxWidth and textAlign = TextAlign.Center.)
17. Add click listeners and implement them in sample app [open]
    - Add event click feedback in sample app
    - Add long press to remove event in sample app [✔] (Long-pressing an event removes it from the view and shows a Toast with the event ID. The event list is managed as Compose state for correct UI updates. Wichtig: Die Event-Liste wird mit `rememberSaveable` gehalten, damit sie bei Recomposition und Konfigurationsänderungen erhalten bleibt und die UI zuverlässig aktualisiert wird.)
17. Add interactions for scaling [open]
18. Testing and validation [open]
19. Documentation and README update [open]

## Continuous Rules

- After each step, ensure a stable state for a git commit
- Comments in code must be in English
- Put components into their own files, avoid large files
- Extend and modify this plan whenever needed to reflect new requirements or findings

## Legend

- [✔] done
- [open] to do

## Recent Completed Work (Steps 11-15)

### Enhanced Grid and Now Indicator Features (Steps 11-14)

- **Horizontal Grid Lines**: Fixed grid lines to span the full width including the time column
- **Dynamic Now Indicator**: 
  - Always visible when current time is within the displayed time range
  - Automatically updates every second showing live time progression
  - Synchronized horizontal line across the entire grid width
- **Scaling Factor Compatibility**: Ensured all time labels and grid elements respect the scaling factor
- **Current Time Display**: 
  - Added HH:mm format current time display in the time column
  - Right-aligned with appropriate padding for better visual appearance
  - Updates dynamically as time progresses
  - Bold styling and error color to match the now indicator line

### Event Rendering Implementation (Step 15)

- **Event Rendering Architecture**: Created dedicated `EventCompose.kt` component following clean separation principles
- **Single Event Support**: Full implementation of `Event.Single` rendering with:
  - Time-based positioning and sizing calculations
  - Proper day column placement
  - Duration-based height scaling
  - Visibility filtering for events outside time range
- **Event Styling**: Complete visual styling with:
  - Background and text colors from event data
  - Rounded corners and appropriate padding
  - Text overflow handling with ellipsis
  - Support for title, subtitle, time display, and custom text fields
- **Configuration Integration**: Respects both `EventConfig` and `WeekViewConfig` settings:
  - Short vs full titles
  - Optional subtitle display
  - Optional time end display
  - Scaling factor compatibility
- **Interactive Foundation**: Click event support prepared for user interactions
- **WeekView Integration**: Updated main component to overlay events on background grid

### Technical Achievements

- Proper synchronization between time labels and grid scrolling
- Accurate positioning calculations respecting scaling factors
- Clean English comments throughout the codebase
- Robust time handling with automatic updates
- Professional calendar-like behavior with fixed day labels and scrollable content
- Event rendering system with proper layering and positioning
- Component separation following clean architecture principles

---

The Compose week view now has functional event rendering capabilities, bringing it to near feature parity with the classic view. Events are properly positioned, styled, and integrated with the existing grid system. Ready to proceed with interaction implementation.

Please update this plan after every step to keep progress transparent.
