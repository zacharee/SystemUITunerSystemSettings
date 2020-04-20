package tk.zwander.systemuituner.systemsettings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

val Context.hasWriteSettings: Boolean
    get() = Settings.System.canWrite(this)

fun Context.launchWriteSettingsActivity() {
    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    intent.data = Uri.parse("package:$packageName")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    Toast.makeText(this, R.string.grant_write_settings, Toast.LENGTH_SHORT).show()
    startActivity(intent)
}