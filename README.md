# android-week-view
Android library for displaying an overview of events for a week like in a schedule(us) or timetable(uk). 

It's the week view initially used in Schedule Deluxe (https://play.google.com/store/apps/details?id=com.tobiasschuerg.stundenplan), 
which is now outfactored in favor of a better modularization.

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
