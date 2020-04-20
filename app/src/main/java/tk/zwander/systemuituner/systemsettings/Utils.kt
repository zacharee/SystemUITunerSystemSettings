package tk.zwander.systemuituner.systemsettings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

val Context.hasWriteSettings: Boolean
    get() = Settings.System.canWrite(this)

fun Context.launchWriteSettingsActivity() {
    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
    intent.data = Uri.parse("package:$packageName")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    startActivity(intent)
}