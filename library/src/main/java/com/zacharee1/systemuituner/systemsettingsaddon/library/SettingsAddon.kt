package com.zacharee1.systemuituner.systemsettingsaddon.library

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import java.util.concurrent.ConcurrentLinkedQueue

val Context.settingsAddon: SettingsAddon
    get() = SettingsAddon.getInstance(this)

class SettingsAddon private constructor(context: Context) : ContextWrapper(context),
    ServiceConnection {
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
        @Synchronized
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
                    packageManager.getServiceInfo(
                        component,
                        PackageManager.ComponentInfoFlags.of(0L)
                    )
                } else {
                    packageManager.getServiceInfo(component, 0)
                }
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

    private val binderListeners = ConcurrentLinkedQueue<BinderListener>()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_PACKAGE_REPLACED,
                Intent.ACTION_PACKAGE_CHANGED,
                Intent.ACTION_PACKAGE_ADDED -> {
                    if (hasService) {
                        bindAddonService()
                    } else {
                        try {
                            unbindAddonService()
                        } catch (_: Throwable) {}
                    }
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    try {
                        unbindAddonService()
                    } catch (_: Throwable) {}
                }
            }
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        binder = ISettingsService.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

    fun bindAddonService(): Boolean {
        val intent = Intent()
        intent.`package` = Constants.ADDON_PACKAGE
        intent.component = ComponentName(Constants.ADDON_PACKAGE, Constants.SERVICE_NAME)

        try {
            unbindService(this)
        } catch (_: Throwable) {}
        return bindService(intent, this, Context.BIND_AUTO_CREATE).also {
            if (!it) {
                try {
                    unbindService(this)
                } catch (_: Throwable) {}
            }
        }
    }

    fun unbindAddonService() {
        unbindService(this)
    }

    fun addBinderListener(listener: BinderListener) {
        binderListeners.add(listener)
    }

    fun removeBinderListener(listener: BinderListener) {
        binderListeners.remove(listener)
    }

    fun bindOnceAvailable() {
        if (hasService) {
            bindAddonService()
        }

        ContextCompat.registerReceiver(
            this,
            packageReceiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
            },
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    interface BinderListener {
        fun onBinderAvailable(binder: ISettingsService)
        fun onBinderUnavailable()
    }
}