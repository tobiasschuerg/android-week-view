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
8. Fine-tune the UI (padding, centering, font size) [open]
9. Implement event rendering in Compose [open]
10. Add interactions (click, context menu, scaling) in Compose [open]
11. Testing and validation [open]
12. Documentation and README update [open]

## Continuous Rules

- After each step, ensure a stable state for a git commit
- Comments in code must be in English
- Put components into their own files, avoid large files
- Extend and modify this plan whenever needed to reflect new requirements or findings

## Legend

- [✔] done
- [open] to do

---

Please update this plan after every step to keep progress transparent.
