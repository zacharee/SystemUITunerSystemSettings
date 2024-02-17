package com.zacharee1.systemuituner.systemsettingsaddon.library

import android.content.AttributionSource
import android.os.Build
import android.os.Bundle
import android.provider.Settings

/**
 * @param provider should be of type [android.content.IContentProvider]
 */
fun listInternal(
    which: SettingsType,
    callingPackage: String?,
    uid: Int,
    provider: Any
): Array<SettingsValue> {
    return try {
        if (which == SettingsType.UNDEFINED) {
            throw IllegalStateException("Invalid settings type!")
        }

        val method = which.callListMethod
        val result = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val source = Class.forName("android.content.AttributionSource")
                    .getConstructor(Int::class.java, String::class.java, String::class.java)
                    .newInstance(uid, callingPackage, null) as AttributionSource

                provider::class.java.getMethod(
                    "call",
                    Class.forName("android.content.AttributionSource"),
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    Bundle::class.java,
                ).invoke(
                    provider,
                    source,
                    Settings.AUTHORITY,
                    method,
                    null,
                    null,
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                provider::class.java.getMethod(
                    "call",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    Bundle::class.java,
                ).invoke(
                    provider,
                    callingPackage,
                    Settings.AUTHORITY,
                    method,
                    null,
                    null,
                )
            }
            else -> {
                provider::class.java.getMethod(
                    "call",
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    Bundle::class.java,
                ).invoke(
                    provider,
                    callingPackage,
                    method,
                    null,
                    null,
                )
            }
        } as? Bundle?

        result?.getStringArrayList("result_settings_list")
            ?.mapToSavedOptions(which)
            ?.toTypedArray() ?: arrayOf()
    } catch (e: Throwable) {
        throw e
    }
}

fun List<String>.mapToSavedOptions(which: SettingsType): List<SettingsValue> {
    return map { line ->
        val (key, value) = line.split("=").let { items ->
            val first = items[0]
            val rest = (if (items.size > 1) items.slice(1 until items.size)
                .joinToString("=") else null).let {
                if (it == "null") null
                else it
            }

            arrayOf(first, rest)
        }
        SettingsValue(which, key!!, value)
    }
}
