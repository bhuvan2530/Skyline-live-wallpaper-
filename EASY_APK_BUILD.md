# Skyline Live Wallpaper — Easy APK Build

This project is prepared so GitHub can build the APK automatically in the cloud.

## Phone-only method
1. Create a GitHub account (if you don't already have one).
2. Create a new empty repository.
3. Upload all files and folders from this project to the repository.
4. Open the repository's **Actions** tab.
5. Select **Build Skyline Live Wallpaper APK**.
6. Tap **Run workflow**.
7. When it finishes, open the workflow run and download the artifact named:
   `Skyline-Live-Wallpaper-APK`
8. Extract the downloaded artifact. It contains `app-debug.apk`.
9. Transfer/install the APK on your OnePlus Nord CE 2 Lite.
10. Go to **Settings → Wallpapers & style → Wallpapers → Live wallpapers** and apply it.

The workflow uses Java 17 and Gradle 8.9 and builds the existing Android project.
