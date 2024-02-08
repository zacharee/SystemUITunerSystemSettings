package com.zacharee1.systemuituner.systemsettingsaddon.library

import android.net.Uri
import android.provider.Settings

enum class SettingsType(val value: Int) {
    UNDEFINED(-1),
    GLOBAL(0),
    SECURE(1),
    SYSTEM(2);

    val callGetMethod: String
        get() = when (this) {
            UNDEFINED -> throw IllegalArgumentException("Invalid settings type!")
            GLOBAL -> Settings.CALL_METHOD_GET_GLOBAL
            SECURE -> Settings.CALL_METHOD_GET_SECURE
            SYSTEM -> Settings.CALL_METHOD_GET_SYSTEM
        }

    val callListMethod: String
        get() = when (this) {
            UNDEFINED -> throw IllegalArgumentException("Invalid settings type!")
            GLOBAL -> Settings.CALL_METHOD_LIST_GLOBAL
            SECURE -> Settings.CALL_METHOD_LIST_SECURE
            SYSTEM -> Settings.CALL_METHOD_LIST_SYSTEM
        }

    val contentUri: Uri
        get() = when (this) {
            UNDEFINED -> throw IllegalArgumentException("Invalid settings type!")
            GLOBAL -> Settings.Global.CONTENT_URI
            SECURE -> Settings.Secure.CONTENT_URI
            SYSTEM -> Settings.System.CONTENT_URI
        }

    companion object {
        const val UNDEFINED_LITERAL = "undefined"
        const val GLOBAL_LITERAL = "global"
        const val SECURE_LITERAL = "secure"
        const val SYSTEM_LITERAL = "system"

        fun fromString(input: String): SettingsType {
            return when (input.lowercase()) {
                GLOBAL_LITERAL -> GLOBAL
                SECURE_LITERAL -> SECURE
                SYSTEM_LITERAL -> SYSTEM
                else -> UNDEFINED
            }
        }

        fun fromValue(value: Int): SettingsType {
            return when (value) {
                0 -> GLOBAL
                1 -> SECURE
                2 -> SYSTEM
                else -> UNDEFINED
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            UNDEFINED -> UNDEFINED_LITERAL
            GLOBAL -> GLOBAL_LITERAL
            SECURE -> SECURE_LITERAL
            SYSTEM -> SYSTEM_LITERAL
        }
    }
}