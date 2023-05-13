package com.zacharee1.systemuituner.systemsettingsaddon.library

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SettingsType : Parcelable {
    GLOBAL,
    SECURE,
    SYSTEM,
}