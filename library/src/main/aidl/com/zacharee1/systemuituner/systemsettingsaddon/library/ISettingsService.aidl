package com.zacharee1.systemuituner.systemsettingsaddon.library;

import com.zacharee1.systemuituner.systemsettingsaddon.library.SettingsType;

interface ISettingsService {
    String readSetting(in SettingsType type, String key) = 1;
    boolean writeSetting(in SettingsType type, String key, String value) = 2;
    boolean canWriteSystem() = 3;
    boolean canWriteGlobalOrSecure() = 4;
    void requestWriteSystem() = 5;

    int addonVersion() = 6;
}