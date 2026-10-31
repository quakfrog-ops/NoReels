# Reels Lock (Android)

Detects when Instagram's Reels screen opens and sends you back to the Home tab.
DMs, Stories and the normal feed still work.

## Build
1. Open this folder in Android Studio (Koala or newer) and let Gradle sync.
2. Plug in your phone (USB debugging on) and press Run, or Build > Build APK(s) and install the APK.

## Enable
1. Open Reels Lock, tap "Enable service".
2. Accessibility > Installed apps > Reels Lock > turn on.
3. Android 13+: if the toggle is greyed out, go to Settings > Apps > Reels Lock > ⋮ > "Allow restricted settings", then try again.

## If it stops working after an Instagram update
Instagram renames internal view IDs now and then. Update the ID lists at the top of
`app/src/main/java/com/example/reelslock/ReelsBlockerService.kt`.
You can find current IDs with Android Studio's Layout Inspector or `uiautomator dump`.

## Privacy
The service is limited to the `com.instagram.android` package and stores nothing except an on/off flag and a daily block counter on your phone.
