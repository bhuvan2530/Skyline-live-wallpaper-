# Skyline Live Wallpaper

Android live wallpaper built from the supplied skyline image.

## What it does
- Uses the supplied photo as the main scene.
- Automatically changes the atmosphere according to the phone's local system clock.
- Dawn/sunrise: brightens toward daytime.
- Day: clear blue atmospheric wash.
- Sunset: warm golden/orange transition.
- Night: dark blue atmosphere, stars and a moving moon.
- Updates once per minute to keep battery use low.
- Portrait-first and works with the OnePlus Nord CE 2 Lite's tall display.

## Important timing note
A true astronomical sunrise/sunset/moonrise/moonset requires the device's date AND geographic location. This version intentionally uses the system clock only, so it does not request location permission. The default schedule is:
05:00–07:00 dawn/sunrise
07:00–16:30 day
16:30–19:00 sunset/golden hour
19:00–05:00 moonlit night

If you want, these four times can be changed in `SkylineWallpaperService.java`.

## Build
Open this folder in Android Studio, let Gradle sync, then Run/Build APK.

The app itself opens Android's Live Wallpaper picker when you press SET LIVE WALLPAPER.
