# GitHub Copilot Agents Instructions

This document provides comprehensive instructions for GitHub Copilot agents working on the Android Week View project. It complements the detailed technical documentation in `.github/copilot-instructions.md`.

## Project Context

**Android Week View** is a Kotlin-based Android library providing customizable week view components for displaying schedules and events. The project is currently undergoing a migration from traditional Android Views to Jetpack Compose while maintaining backward compatibility.

### Key Repository Information

- **Primary Language**: Kotlin (Java 17 toolchain)
- **Build System**: Gradle with Android Gradle Plugin
- **Architecture**: Clean Architecture with separated data, view, and utility layers
- **Current State**: Dual implementation (Legacy View + Modern Compose)
- **Distribution**: JitPack

## Agent Guidelines

### 1. Understanding the Codebase

Before making any changes:
- Review the existing `.github/copilot-instructions.md` for detailed technical context
- Check `MIGRATION_PLAN.md` to understand the current migration status
- Examine the project structure: `app/` (sample), `library/` (main code), `meta/` (assets)
- Understand the dual implementation approach (View-based vs Compose-based)

### 2. Code Quality Standards

**Formatting and Linting**:
```bash
./gradlew ktlintFormat  # Auto-format code
./gradlew ktlintCheck   # Verify code style
```

**Kotlin Conventions**:
- Use data classes for immutable data models
- Prefer sealed classes for type-safe hierarchies
- Follow camelCase for functions/properties, PascalCase for classes
- Use extension functions to enhance existing classes
- Add KDoc comments for public APIs

**Architecture Principles**:
- Maintain separation between data, view, and utility packages
- Prefer composition over inheritance
- Keep components in separate files (avoid large files)
- Follow existing patterns for event handling and configuration

**Commit Message Standards**:
Use conventional commits format for all commits:
```
<type>: <summary>

Examples:
feat: add swipe gesture support to Compose WeekView
fix: resolve event overlap calculation in legacy view
docs: update README with new Compose API examples
refactor: extract time calculation utilities
test: add unit tests for event positioning
```

### 3. Making Changes

**Before Coding**:
1. Always run existing tests to understand current state
2. Check both View-based and Compose implementations when relevant
3. Verify changes work with the sample app in `app/` module
4. Consider backward compatibility implications

**During Development**:
- Make minimal, focused changes
- Update both implementations if the change affects core functionality
- Add appropriate deprecation warnings for Legacy View features
- Test changes in the sample app
- Update documentation if APIs change

**Testing Strategy**:
- Unit tests are in `library/src/test/java/`
- Use the sample app for integration testing
- Test both View and Compose implementations
- Verify touch interactions, scaling, and animations

### 4. Migration Awareness

This project is actively migrating from View-based to Compose. Be aware that:

- **View-based implementation** (deprecated but maintained)
  - Located in `library/src/main/java/de/tobiasschuerg/weekview/view/`
  - Uses traditional Android Views and custom drawing
  - Should only receive bug fixes, no new features

- **Compose implementation** (recommended, under development)
  - Located in `library/src/main/java/de/tobiasschuerg/weekview/compose/`
  - Modern Jetpack Compose approach
  - Active development for new features

- **Current Migration Status**: Check `MIGRATION_PLAN.md` for latest progress

### 5. Common Development Tasks

**Adding New Features**:
- Prioritize Compose implementation
- Add to View implementation only if required for compatibility
- Update sample app to demonstrate the feature
- Add configuration options to appropriate Config classes

**Bug Fixes**:
- Fix in both implementations if the bug exists in both
- Verify fix with sample app
- Add regression tests where possible

**API Changes**:
- Maintain backward compatibility
- Use `@Deprecated` annotations appropriately
- Update documentation and migration guides
- Version changes according to semantic versioning

### 6. Time and Event Handling

The library uses `java.time` API with desugaring support:
- Use `LocalDate`, `LocalTime`, `Duration` consistently
- Handle time zones carefully (library works with local time concepts)
- Respect the `TimeSpan` data class for time intervals
- Follow existing patterns for event creation and management

### 7. Performance Considerations

- Reuse event views when possible in View-based implementation
- Minimize layout passes during scrolling
- Use efficient drawing for background grids
- Consider event virtualization for large datasets
- Optimize event overlap calculations

### 8. Documentation Updates

When making changes that affect users:
- Update `README.md` for API changes
- Add usage examples for new features
- Update migration guides if needed
- Keep `.github/copilot-instructions.md` current with architectural changes

### 9. Sample App Usage

The `app/` module contains a comprehensive sample:
- `SampleActivity.kt`: Main chooser between implementations
- `ClassicWeekViewActivity.kt`: Legacy View-based example
- `ComposeWeekViewActivity.kt`: Modern Compose example
- `EventCreator.kt`: Helper for generating sample data

Use the sample app to:
- Test new features interactively
- Verify bug fixes
- Demonstrate proper API usage
- Validate performance improvements

### 10. Build and Release

**Local Development**:
- Use `./gradlew build` for full builds
- Run `./gradlew ktlintFormat` before commits
- Test on different Android API levels (min 21, target 34)

**Version Management**:
- Version defined in root `build.gradle` as `lib_version`
- Follow semantic versioning
- JitPack handles distribution automatically

## Quick Reference

### Key Files to Understand
- `library/src/main/java/de/tobiasschuerg/weekview/data/Event.kt` - Event data models
- `library/src/main/java/de/tobiasschuerg/weekview/view/WeekView.kt` - Legacy main component
- `library/src/main/java/de/tobiasschuerg/weekview/compose/WeekViewCompose.kt` - Modern main component
- `MIGRATION_PLAN.md` - Current migration status
- `README.md` - Public API documentation

### Common Commands
```bash
./gradlew ktlintFormat    # Format code
./gradlew ktlintCheck     # Check code style
./gradlew clean build     # Full build
./gradlew test           # Run tests
```

### Package Structure
```
de.tobiasschuerg.weekview/
├── data/          # Event models, configurations
├── util/          # Helper classes, extensions
├── view/          # Legacy View-based components (deprecated)
└── compose/       # Modern Compose components (recommended)
```

## Agent Success Criteria

A successful agent contribution should:
- ✅ Follow existing code patterns and architecture
- ✅ Work with both implementations when applicable
- ✅ Include proper testing and validation
- ✅ Maintain backward compatibility
- ✅ Include appropriate documentation updates
- ✅ Pass ktlint formatting checks
- ✅ Demonstrate functionality in sample app

For detailed technical specifications, architecture details, and comprehensive coding standards, always refer to `.github/copilot-instructions.md`.