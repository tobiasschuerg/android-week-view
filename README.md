[![](https://jitpack.io/v/tobiasschuerg/android-week-view.svg)](https://jitpack.io/#tobiasschuerg/android-week-view)

# Android Week View
Android library for displaying an overview of events for a week like in a schedule(us) or timetable(uk). 

It's the week view initially used in Schedule Deluxe (https://play.google.com/store/apps/details?id=com.tobiasschuerg.stundenplan), 
which is now outfactored in favor of a better modularization.

![alt text][logo]

[logo]: https://lh3.googleusercontent.com/F-8Kn4XQoC-_fh6p_2T9LPFlWO5hXtY9boCX6KuFSHKa2Cp4rVP0xIM_xkl8SnuPhw=h400-rw
 "Screenshot"


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
