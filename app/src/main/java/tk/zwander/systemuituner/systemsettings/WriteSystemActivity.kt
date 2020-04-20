package tk.zwander.systemuituner.systemsettings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings

class WriteSystemActivity : AppCompatActivity() {
    private val key by lazy { intent.getStringExtra(WriteSystemListener.EXTRA_KEY) }
    private val value by lazy { intent.getStringExtra(WriteSystemListener.EXTRA_VALUE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action != WriteSystemListener.ACTION_WRITE_SYSTEM
            || key.isNullOrBlank()
        ) {
            finish()
            return
        }

        if (!hasWriteSettings) {
            launchWriteSettingsActivity()
            setResultAndFinish(false, Exception("WRITE_SETTINGS not granted"))
            return
        }

        val success = try {
            Settings.System.putString(contentResolver, key, value)
            true to null
        } catch (e: Exception) {
            false to e
        }

        setResultAndFinish(success.first, success.second)
    }

    fun setResultAndFinish(success: Boolean, exception: Exception? = null) {
        setResult(
            if (success) Activity.RESULT_OK else Activity.RESULT_CANCELED,
            Intent(WriteSystemListener.ACTION_WRITE_SYSTEM_RESULT).putExtras(intent)
                .putExtra(WriteSystemListener.EXTRA_EXCEPTION, exception)
        )

        finish()
    }
}