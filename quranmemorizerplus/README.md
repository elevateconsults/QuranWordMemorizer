# Quran Word Memorizer Plus (Fresh App)

A fresh Kotlin + Jetpack Compose Android app scaffold with a clean, minimalist interface.

- Application ID: `store.jaranation.qwm2`
- Min SDK: 24, Target/Compile SDK: 34
- UI: Jetpack Compose (Material 3)

## Run locally (Android Studio)
1. Open the repository in Android Studio.
2. Select the run configuration for the module `quranmemorizerplus`.
3. Choose an emulator or physical device.
4. Run ▶

## Build via CLI (local)
- Windows PowerShell (from repo root):
  - `./gradlew :quranmemorizerplus:assembleDebug`
  - APK path: `quranmemorizerplus/build/outputs/apk/debug/quranmemorizerplus-debug.apk`

## CI Builds (GitHub Actions)
- Workflow: `.github/workflows/android-ci-qmp.yml`
- On push/PR to `main` or `master`, it builds the debug APK and uploads it as an artifact named `quranmemorizerplus-debug-apk`.
- Download from the run’s Artifacts section and install on your device.

## Current Screens
- Home (cards): Practice Now, Word Bank, Progress, Settings. Navigation is scaffolded and ready to expand.

## Next Steps (suggested)
- Add navigation targets for each card and basic placeholder screens.
- Add Room database (Word entity/DAO/DB) and start wiring Word Bank.
- Add a Dev/Test screen to trigger a test notification and seed sample data.
- Introduce WorkManager for scheduled notifications.

## Notes
- Launcher icon is a minimalist adaptive icon.
- Theme is set to `Theme.QWMPlus` (Material 3) with a simple palette.
