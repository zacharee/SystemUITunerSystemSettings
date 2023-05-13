package com.zacharee1.systemuituner.systemsettingsaddon.library

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import java.util.concurrent.ConcurrentSkipListSet

class SettingsAddon private constructor(context: Context) : ContextWrapper(context), ServiceConnection {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: SettingsAddon? = null

        @Synchronized
        fun getInstance(context: Context): SettingsAddon {
            return instance ?: SettingsAddon(context.applicationContext ?: context).apply {
                instance = this
            }
        }
    }

    var binder: ISettingsService? = null
        private set(value) {
            field = value

            binderListeners.forEach { listener ->
                if (value != null) {
                    listener.onBinderAvailable(value)
                } else {
                    listener.onBinderUnavailable()
                }
            }
        }

    val binderAvailable: Boolean
        get() = binder.run { this != null && this.asBinder().isBinderAlive }

    val hasService: Boolean
        get() {
            val component = ComponentName(Constants.ADDON_PACKAGE, Constants.SERVICE_NAME)

            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getServiceInfo(component, PackageManager.ComponentInfoFlags.of(0L))
                } else {
                    @Suppress("DEPRECATION")
                    packageManager.getServiceInfo(component, 0)
                }
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    private val binderListeners = ConcurrentSkipListSet<BinderListener>()

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        binder = ISettingsService.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

    fun bindAddonService(connection: ServiceConnection): Boolean {
        val intent = Intent()
        intent.`package` = Constants.ADDON_PACKAGE
        intent.component = ComponentName(Constants.ADDON_PACKAGE, Constants.SERVICE_NAME)

        return bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun addBinderListener(listener: BinderListener) {
        binderListeners.add(listener)
    }

    fun removeBinderListener(listener: BinderListener) {
        binderListeners.remove(listener)
    }

    interface BinderListener {
        fun onBinderAvailable(binder: ISettingsService)
        fun onBinderUnavailable()
    }
}