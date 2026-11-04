package com.example.memoryleak.leaks.async

import android.os.Handler
import android.os.Looper
import android.os.Message

class InnerClassHandlerLeak {
    // LEAK: Non-static Handler or Runnable inner class holding an implicit reference to outer Activity.
    // Delayed messages stay in Looper queue keeping Handler -> Activity alive for minutes or hours!
    private val leakyHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // Processing message
        }
    }

    fun triggerLeak(): String {
        val runnable = Runnable {
            // Long running work
        }
        // Post a delayed message for 10 minutes (600,000ms)
        leakyHandler.postDelayed(runnable, 600000L)
        return "Posted delayed Runnable (10 minutes) via Handler inner class holding implicit Activity context."
    }

    fun triggerFix(): String {
        // FIX: Remove callbacks and messages in onDestroy() or use static Handler with WeakReference.
        leakyHandler.removeCallbacksAndMessages(null)
        return "Fixed! Called handler.removeCallbacksAndMessages(null) to clear message queue."
    }
}
