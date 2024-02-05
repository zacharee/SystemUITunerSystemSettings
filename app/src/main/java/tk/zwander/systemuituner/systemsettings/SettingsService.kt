package tk.zwander.systemuituner.systemsettings

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import com.zacharee1.systemuituner.systemsettingsaddon.library.ISettingsService
import com.zacharee1.systemuituner.systemsettingsaddon.library.SettingsType

class SettingsService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        return object : ISettingsService.Stub() {
            override fun readSetting(type: SettingsType?, key: String?): String? {
                return when (type) {
                    SettingsType.GLOBAL -> Settings.Global.getString(contentResolver, key)
                    SettingsType.SECURE -> Settings.Secure.getString(contentResolver, key)
                    SettingsType.SYSTEM -> Settings.System.getString(contentResolver, key)
                    else -> throw IllegalArgumentException("Invalid settings type!")
                }
            }

            override fun writeSetting(type: SettingsType?, key: String?, value: String?): Boolean {
                return when (type) {
                    SettingsType.GLOBAL -> {
                        canWriteGlobalOrSecure() && try {
                            Settings.Global.putString(contentResolver, key, value)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    SettingsType.SECURE -> {
                        canWriteGlobalOrSecure() && try {
                            Settings.Secure.putString(contentResolver, key, value)
                        } catch (e: Exception) {
                            false
                        }
                    }
                    SettingsType.SYSTEM -> {
                        val token = Binder.clearCallingIdentity()

                        try {
                            if (canWriteSystem()) {
                                try {
                                    Settings.System.putString(contentResolver, key, value)
                                } catch (e: Exception) {
                                    false
                                }
                            } else {
                                requestWriteSystem()
                                false
                            }
                        } finally {
                            Binder.restoreCallingIdentity(token)
                        }
                    }
                    else -> throw IllegalArgumentException("Invalid settings type!")
                }
            }

            override fun canWriteGlobalOrSecure(): Boolean {
                return checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
            }

            override fun canWriteSystem(): Boolean {
                return Settings.System.canWrite(this@SettingsService)
            }

            override fun requestWriteSystem() {
                Handler(Looper.getMainLooper()).post {
                    launchWriteSettingsActivity()
                }
            }

            override fun addonVersion(): Int {
                return BuildConfig.VERSION_CODE
            }
        }
    }
}