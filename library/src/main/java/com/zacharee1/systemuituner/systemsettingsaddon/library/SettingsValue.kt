package com.zacharee1.systemuituner.systemsettingsaddon.library

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsValue(
    val type: SettingsType,
    val key: String,
    val value: String?,
) : Parcelable
