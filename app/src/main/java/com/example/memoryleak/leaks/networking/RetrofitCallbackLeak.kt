package com.example.memoryleak.leaks.networking

import android.os.Handler
import android.os.Looper

interface NetworkCallback {
    fun onSuccess(data: String)
    fun onError(error: String)
}

class RetrofitCallbackLeak {
    private var pendingCallback: NetworkCallback? = null

    fun triggerLeak(callback: NetworkCallback): String {
        pendingCallback = callback
        // LEAK: Asynchronous network call (simulated) holding UI callback reference after screen leaves.
        Handler(Looper.getMainLooper()).postDelayed({
            pendingCallback?.onSuccess("Network data loaded")
        }, 30000L)
        return "Dispatched asynchronous network call holding UI NetworkCallback reference (30 second timeout)."
    }

    fun triggerFix(): String {
        // FIX: Cancel pending network request and nullify callback.
        pendingCallback = null
        return "Fixed! Canceled network callback subscription and nullified listener."
    }
}
