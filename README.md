[![](https://jitpack.io/v/tobiasschuerg/android-week-view.svg)](https://jitpack.io/#tobiasschuerg/android-week-view)
[![Build Status](https://www.bitrise.io/app/6ba47c24369dd52a/status.svg?token=XyF0AXasZwgKuoub_tJUYA&branch=master)](https://www.bitrise.io/app/6ba47c24369dd52a)

# Android Week View
Android library for displaying an overview of events for a week like in a schedule(us) or timetable(uk). 

It's the week view initially used in Schedule Deluxe (https://play.google.com/store/apps/details?id=com.tobiasschuerg.stundenplan), 
which is now outfactored in favor of a better modularization.

<img src="https://github.com/tobiasschuerg/android-week-view/blob/master/meta/device-2018-02-24-121341.png" height="400">

# Get it

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
