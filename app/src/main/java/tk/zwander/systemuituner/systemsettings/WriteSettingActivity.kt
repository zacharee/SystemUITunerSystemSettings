package tk.zwander.systemuituner.systemsettings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings

class WriteSettingActivity : AppCompatActivity() {
    private val type by lazy { intent.getStringExtra(WriteSettingListener.EXTRA_TYPE) }
    private val key by lazy { intent.getStringExtra(WriteSettingListener.EXTRA_KEY) }
    private val value by lazy { intent.getStringExtra(WriteSettingListener.EXTRA_VALUE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action != WriteSettingListener.ACTION_WRITE_SETTING ||
            type.isNullOrBlank() || key.isNullOrBlank()) {
            setResultAndFinish(false, Exception("Type or Key was null!"))
            return
        }

        if (!hasWriteSettings) {
            launchWriteSettingsActivity()
            setResultAndFinish(false, Exception("WRITE_SETTINGS not granted"))
            return
        }

        val success = try {
            when (type.lowercase()) {
                "global" -> {
                    Settings.Global.putString(contentResolver, key, value)
                }
                "secure" -> {
                    Settings.Secure.putString(contentResolver, key, value)
                }
                "system" -> {
                    Settings.System.putString(contentResolver, key, value)
                }
                else -> {}
            }
            true to null
        } catch (e: Exception) {
            false to e
        }

        setResultAndFinish(success.first, success.second)
    }

    private fun setResultAndFinish(success: Boolean, exception: Exception? = null) {
        setResult(
            if (success) Activity.RESULT_OK else Activity.RESULT_CANCELED,
            Intent(WriteSettingListener.ACTION_WRITE_SETTING_RESULT).putExtras(intent)
                .putExtra(WriteSettingListener.EXTRA_EXCEPTION, exception)
        )

        finish()
    }
}