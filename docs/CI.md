# Android CI: Build APK and (Optionally) Distribute via Firebase

This repository is configured with GitHub Actions to build APKs on every push and PR.
You can optionally distribute builds to Firebase App Distribution by adding secrets.

## What you get
- Debug APK artifact on every push/PR to `main` or `master`.
- Optional signed Release APK artifact when signing secrets are present.
- Optional automatic upload to Firebase App Distribution (Debug or Release APK).

## Triggering the workflow
- Push or open a PR to `main` or `master`.
- Or run manually: Actions → Android CI → Run workflow.

## Downloading the APK artifacts
- Go to the specific workflow run → Artifacts section.
- Download `app-debug-apk` (and/or `app-release-apk` when available).
- Install on your phone: enable "Install unknown apps", then share the APK to the device and install.

## Optional: Signing for Release APK
If you want a signed Release APK artifact, add the following GitHub Secrets:
- `RELEASE_KEYSTORE_BASE64` — Base64 of your keystore file (e.g., `release.keystore`).
- `RELEASE_KEYSTORE_PASSWORD`
- `RELEASE_KEY_ALIAS`
- `RELEASE_KEY_PASSWORD`

The workflow will decode the keystore and build a signed `app-release.apk`.

### Generate a keystore (if you don't have one)
```bash
keytool -genkeypair \
  -v -keystore release.keystore \
  -alias your_alias \
  -keyalg RSA -keysize 2048 -validity 10000
```

### Base64-encode the keystore
On macOS/Linux:
```bash
base64 -w 0 release.keystore > release.keystore.base64
```
On Windows PowerShell:
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File -Encoding ascii release.keystore.base64
```
Copy the file contents into the `RELEASE_KEYSTORE_BASE64` secret.

## Optional: Firebase App Distribution
Add these GitHub Secrets to enable Firebase distribution:
- `FIREBASE_TOKEN` — CI auth token for Firebase CLI.
- `FIREBASE_APP_ID_ANDROID` — Your Android App ID (starts with `1:`) from Firebase Console → Project settings → General.
- `FIREBASE_GROUPS` — (optional) comma-separated tester group names, defaults to `testers`.

The workflow distributes the Debug APK by default. If a Release APK is present, it will distribute that instead.

### Obtain `FIREBASE_TOKEN`
Install Firebase CLI and log in once locally, then print a CI token:
```bash
npm i -g firebase-tools
firebase login
firebase login:ci
```
Copy the displayed token into the `FIREBASE_TOKEN` secret.

## Files added/used by CI
- Workflow: `.github/workflows/android-ci.yml`
- Temporary signing file (created during CI run): `local-signing.properties`
- Keystore (decoded only in CI): `release.keystore` (not committed)

## Notes
- Build uses JDK 17 and Android SDK 34.
- Workflow builds module `:app` with Gradle wrapper.
- If your default branch is not `main` or `master`, adjust the workflow `on.push.branches` accordingly.
