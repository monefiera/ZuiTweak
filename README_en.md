# ZuiTweak

[Korean Version](README.md)

## Overview
**ZuiTweak** is an Xposed module that applies various tweaks to Lenovo's Zui-based ROMs.

## Key Features
- **Common**
  1. **Enable Camera Silent Mode:** Allows the default camera to operate in silent mode through the sound settings.
  2. **Remove Lockscreen Shortcuts:** Removes the shortcuts at the bottom left and right of the lock screen.
  3. **Remove Unused Game Service Icons:** Removes unnecessary icons from the game service.
  4. **Add Hidden Display Settings:** Adds all settings to the disabled and hidden display settings.
  5. **Disable Taskbar Display:** Removes the taskbar display feature during gesture navigation. (Unfuck Zui feature)
     * Note: 3-button navigation buttons will disappear.
  6. **Change Notification Icons:** Changes the background of notification icons to match the theme. (Unfuck Zui feature)
  7. **Disable Dolby Atmos for Built-in Speakers:** Allows disabling Dolby Atmos for built-in speakers. (Unfuck Zui feature)
  8. **Disable Force Stop for Apps:** Prevents apps from being forcibly stopped when removed from the App Switcher. (Unfuck Zui feature)

- **ROW**
  1. **Enable Multiple Space:** Enables the Multiple Space feature on ROW-based ROMs.
     * Note: Requires installation of [ZuiTweak-magisk](https://github.com/forumi0721/ZuiTweak-magisk).
  2. **Enable WLAN Hotspot:** Enables the WLAN Hotspot feature.
  3. **Enable Smart Split:** Enables the Smart Split feature.
  4. **Enable Battery Power Consumption Warning:** Enables the battery power consumption warning feature.

- **PRC**
  1. **Enable Korean Language Setting:** Allows selecting the Korean language in the settings on PRC-based ROMs.
     * Note: Requires [ZuiTweak-magisk](https://github.com/forumi0721/ZuiTweak-magisk) or CTS to enable Korean.
  2. **Remove Launcher Online Search:** Removes the online search feature from the launcher on PRC-based ROMs and switches to PRC mode.
     * Note: To remove the left dot, install [Entertainment Space](https://play.google.com/store/search?q=entertainment+space&c=apps&hl=en).
  3. **Fix DocumentsUI Crash:** Fixes the crash issue that occurs when dragging with a mouse in DocumentsUI on PRC-based ROMs.
  4. **Enable Smart Rotation:** Enables the Smart Rotation feature.
  5. **Disable Study Launcher:** Hides the Study Launcher from the settings screen.
  6. **Remove PRC Information:** Removes PRC-related information displayed in the tablet info, etc.
  7. **Fix Battery Icon Disappearance:** Restores the battery icon that disappears when test_mode is enabled on PRC-based ROMs.
  8. **Disable Virus Scan:** Disables the Virus Scan feature of SafeCenter. (Unfuck Zui feature)
  9. **Change Package Installer:** Changes the Package Installer to an AOSP style. (Unfuck Zui feature)
  10. **Change Permission Controller:** Changes the Permission Controller to an AOSP style. (Unfuck Zui feature)

- **TB371FC**
  1. **Change Keyboard Shortcuts:** Adjusts the keyboard shortcuts (fn+E, fn+S, fn+P) to work correctly on PRC-based ROMs.

## Source Code
The source code is available on GitHub: [ZuiTweak Source Code](https://github.com/forumi0721/ZuiTweak)

## License
This project is licensed under the Apache License v2.0. For more details, please refer to the [LICENSE](https://github.com/forumi0721/ZuiTweak/blob/main/LICENSE).

## Installation Instructions
1. **Install LSPosed**
   - ZuiTweak requires LSPosed to be installed first. (Available on rooted devices only)
2. **Download and Install ZuiTweak**
   - Download the latest version from the [Release Page](https://github.com/forumi0721/ZuiTweak/releases) and install it.
3. **Activate in LSPosed**
   - Activate the ZuiTweak module in LSPosed.
4. **Configure in ZuiTweak**
   - Launch ZuiTweak from the launcher and activate the desired features.

## Important Notes
- The silent mode feature should be used within the limits of legal regulations.
- Hidden issues and bugs may exist.

## Contact
If you encounter any issues or have questions while using the app, please report them via the [Issues Page](https://github.com/forumi0721/ZuiTweak/issues).

