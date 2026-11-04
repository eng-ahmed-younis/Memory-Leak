package com.example.memoryleak.leaks.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class UnregisteredReceiverLeak(private val context: Context) {
    private var registeredReceiver: BroadcastReceiver? = null

    fun triggerLeak(): String {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                // Handle broadcast
            }
        }
        registeredReceiver = receiver
        // LEAK: Registering BroadcastReceiver with Context but never calling unregisterReceiver.
        context.registerReceiver(receiver, IntentFilter("com.example.ACTION_DEMO_LEAK"), Context.RECEIVER_NOT_EXPORTED)
        return "BroadcastReceiver registered via Activity Context without calling unregisterReceiver."
    }

    fun triggerFix(): String {
        registeredReceiver?.let {
            try {
                context.unregisterReceiver(it)
                registeredReceiver = null
            } catch (e: Exception) {
                // Already unregistered or not registered
            }
        }
        return "Fixed! Unregistered BroadcastReceiver successfully when lifecycle ended."
    }
}
