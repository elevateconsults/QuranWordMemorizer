# Build and install Quran Memorizer Plus (Debug) and launch it on a connected device
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\scripts\build_and_install_qmp.ps1
# Optional switches:
#   -ClearData      Clears app data before launching
#   -AssembleOnly   Only builds the APK without installing/launching
# Requires:
#   - ADB (Android platform-tools) on PATH, and a device connected with USB debugging enabled
#   - Gradle Wrapper (gradlew) in repo root

param(
    [switch]$ClearData = $false,
    [switch]$AssembleOnly = $false
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

function Assert-CommandExists($cmd) {
    if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
        throw "Required command '$cmd' was not found on PATH. Please install Android Platform-Tools and/or ensure it's on PATH."
    }
}

function Assert-DeviceConnected() {
    $out = adb devices | Select-String -Pattern "\tdevice$"
    if (-not $out) {
        throw "No Android device detected. Ensure USB debugging is enabled and authorize the PC. Run: adb devices"
    }
}

# Move to repo root (this script is expected to be run from repo root; adapt if needed)
$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$repoRoot = Split-Path -Parent $repoRoot  # scripts/ -> repo root
Set-Location $repoRoot

# Ensure gradlew exists
if (-not (Test-Path "$repoRoot/gradlew")) {
    throw "gradlew not found in repo root: $repoRoot"
}

# If only assembling, we don't need ADB/device
if (-not $AssembleOnly) {
    Assert-CommandExists adb
    Assert-DeviceConnected
}

Write-Host "==> Building :quranmemorizerplus Debug APK..." -ForegroundColor Cyan
./gradlew :quranmemorizerplus:assembleDebug

if ($AssembleOnly) {
    Write-Host "==> Assemble completed. APK at: quranmemorizerplus/build/outputs/apk/debug/quranmemorizerplus-debug.apk" -ForegroundColor Green
    exit 0
}

Write-Host "==> Installing to connected device..." -ForegroundColor Cyan
./gradlew :quranmemorizerplus:installDebug

if ($ClearData) {
    Write-Host "==> Clearing app data..." -ForegroundColor Yellow
    adb shell pm clear store.jaranation.qwm2 | Out-Null
}

Write-Host "==> Launching app..." -ForegroundColor Cyan
adb shell am start -n store.jaranation.qwm2/.MainActivity | Out-Null

Write-Host "==> Done. App should now be visible on the device." -ForegroundColor Green
