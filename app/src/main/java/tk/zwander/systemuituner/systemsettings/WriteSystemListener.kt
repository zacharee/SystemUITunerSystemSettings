package tk.zwander.systemuituner.systemsettings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

class WriteSystemListener : BroadcastReceiver() {
    companion object {
        const val ACTION_WRITE_SYSTEM = "com.zacharee1.systemuituner.WRITE_SYSTEM"
        const val ACTION_WRITE_SYSTEM_RESULT = "com.zacharee1.systemuituner.WRITE_SYSTEM_RESULT"

        const val EXTRA_KEY = "WRITE_SYSTEM_KEY"
        const val EXTRA_VALUE = "WRITE_SYSTEM_VALUE"
        const val EXTRA_EXCEPTION = "WRITE_SYSTEM_EXCEPTION"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_WRITE_SYSTEM) {
            val key = intent.getStringExtra(EXTRA_KEY) ?: return
            val value = intent.getStringExtra(EXTRA_VALUE)

            Settings.System.putString(context.contentResolver, key, value)
        }
    }
}