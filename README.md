[![](https://jitpack.io/v/tobiasschuerg/android-week-view.svg)](https://jitpack.io/#tobiasschuerg/android-week-view)
[![Build Status](https://www.bitrise.io/app/6ba47c24369dd52a/status.svg?token=XyF0AXasZwgKuoub_tJUYA&branch=master)](https://www.bitrise.io/app/6ba47c24369dd52a)

# Android Week View

Android library for displaying an overview of events for a week
like in a schedule(us) or timetable(uk).

It's the week view initially used in Schedule
Deluxe (https://play.google.com/store/apps/details?id=com.tobiasschuerg.stundenplan),
which is now outsourced in favor of a better modularization.

<img src="https://github.com/tobiasschuerg/android-week-view/blob/master/meta/device-2018-02-24-121341.png" height="400">

## Usage:

See `SampleActivity.kt` for how to use the week view.

### In short:

1. Attach the view to your layout:

```xml

<de.tobiasschuerg.weekview.view.WeekView android:id="@+id/week_view"
    android:layout_width="match_parent" android:layout_height="wrap_content"
    app:accent_color="@color/colorAccent" app:start_hour="8" app:end_hour="15" />
```

2. Configure how events are displayed (optional):

```kotlin
val config = EventConfig(showSubtitle = false, showTimeEnd = false)
weekView.eventConfig = config
weekView.setShowNowIndicator(true)
```

3Add events:

```kotlin
val nowEvent = Event.Single(
    id = 1337,
    date = LocalDate.now(),
    title = "Dentist Appointment",
    shortTitle = "DENT",
    timeSpan = TimeSpan.of(LocalTime.of(10, 0), Duration.ofHours(1)),
    backgroundColor = Color.RED,
    textColor = Color.WHITE
)
weekView.addEvent(nowEvent)
```

## Desugaring:

Starting from version 1.8.0, Android Week View has switched from JakeWharton's ThreeTen Backport
to desugaring for `java.time`-API.

## Get it

1. Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}
```

2. Step 2. Add the dependency

```gradle
dependencies {
  compile 'com.github.tobiasschuerg:android-week-view:-SNAPSHOT'
}
```
