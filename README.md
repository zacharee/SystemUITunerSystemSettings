# SystemUI Tuner Add-On
This app is an add-on app for [SystemUI Tuner](https://github.com/zacharee/Tweaker) that can be used to read and write restricted settings that SystemUI Tuner can't normally interact with.

With the release of Android 6, Google greatly restricted which values apps could change in Settings.System. However, for backwards-compatibility, these restrictions don't apply to apps that still target Android 5.1 and earlier.

In later versions of Android, Google also started restricting which values could be read from the secure settings by third-party applications. For whatever reason, though, apps marked as test-only can still read these restricted settings.

This add-on targets API 22 (Android 5.1) and is built as test-only to bypass both restrictions when needed.

## Installation
Downloads are available from the [Releases](https://github.com/zacharee/SystemUITunerSystemSettings/releases) page.

Since this add-on is marked as test-only, installation isn't as simple as opening the APK on your device. Android blocks test installations by default without a specific flag.

In order to install the add-on, you'll either need to use [Install with Options](https://github.com/zacharee/InstallWithOptions) (recommended for Android 11 and later) _or_ ADB. 

You only need to use one of the methods below.

### Option 1: Install with Options
1. Download the add-on APK from the Releases page.
2. Install "Install with Options" on your device.
3. Open the app and follow the instructions to set up Shizuku.
4. Select the "Allow Test" and "Replace Existing" options.
5. Select "Choose Files" and choose the downloaded add-on APK.
6. Select "Install" and let the process complete.

### Option 2: ADB
Use the following command (note the `-t` flag):
```bash
adb install -r -t /path/to/add-on.apk
```

## Why a separate app?
SystemUI Tuner is published on the Google Play Store, and so it needs to comply to the Google's publishing rules.

These rules include:
- A minimum-accepted target SDK for app updates (usually the latest version of Android's API number minus 2).
- Only allowing non-debuggable and non-test APKs.
- Policy-based restrictions on what apps can do (i.e., even if the Android system lets an app do something, the Play Store might reject it).

The functionality of this add-on and how it achieves it would prevent SystemUI Tuner from being on the Play Store if it were integrated into the main app.

In addition, since most options are readable and writeable with the WRITE_SECURE_SETTINGS permission used in SystemUI Tuner, this add-on's functionality isn't usually needed.
