package tk.zwander.systemuituner.systemsettings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

class WriteSettingListener : BroadcastReceiver() {
    companion object {
        const val ACTION_WRITE_SETTING = "com.zacharee1.systemuituner.WRITE_SETTING"
        const val ACTION_WRITE_SETTING_RESULT = "com.zacharee1.systemuituner.WRITE_SETTING_RESULT"

        const val EXTRA_TYPE = "WRITE_SETTING_TYPE"
        const val EXTRA_KEY = "WRITE_SETTING_KEY"
        const val EXTRA_VALUE = "WRITE_SETTING_VALUE"
        const val EXTRA_EXCEPTION = "WRITE_SETTING_EXCEPTION"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_WRITE_SETTING) {
            if (!context.hasWriteSettings) {
                context.launchWriteSettingsActivity()
                return
            }

            val type = intent.getStringExtra(EXTRA_TYPE) ?: return
            val key = intent.getStringExtra(EXTRA_KEY) ?: return
            val value = intent.getStringExtra(EXTRA_VALUE)

            val exception = try {
                when (type.lowercase()) {
                    "global" -> {
                        Settings.Global.putString(context.contentResolver, key, value)
                    }

                    "secure" -> {
                        Settings.Secure.putString(context.contentResolver, key, value)
                    }

                    "system" -> {
                        Settings.System.putString(context.contentResolver, key, value)
                    }

                    else -> {
                        throw IllegalArgumentException("Invalid settings type!")
                    }
                }
                null
            } catch (e: Exception) {
                e
            }

            context.sendBroadcast(
                Intent(ACTION_WRITE_SETTING_RESULT)
                    .setPackage("com.zacharee1.systemuituner")
                    .putExtras(intent)
                    .putExtra(EXTRA_EXCEPTION, exception),
                "com.zacharee1.systemuituner.permission.WRITE_SETTINGS"
            )
        }
    }
}