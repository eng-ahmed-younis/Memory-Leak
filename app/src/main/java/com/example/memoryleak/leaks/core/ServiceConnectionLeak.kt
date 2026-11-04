package com.example.memoryleak.leaks.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class ServiceConnectionLeak(private val context: Context) {
    private var activeConnection: ServiceConnection? = null

    fun triggerLeak(): String {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
            override fun onServiceDisconnected(name: ComponentName?) {}
        }
        activeConnection = connection
        val intent = Intent().apply {
            setClassName(context.packageName, "com.example.memoryleak.DummyService")
        }
        // LEAK: Binding to service without calling unbindService when component finishes.
        try {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            // Simulated binding
        }
        return "Bound ServiceConnection with Activity Context without invoking unbindService()."
    }

    fun triggerFix(): String {
        activeConnection?.let {
            try {
                context.unbindService(it)
            } catch (e: Exception) {
                // Handled if not bound
            }
            activeConnection = null
        }
        return "Fixed! Invoked unbindService() to release ServiceConnection."
    }
}
