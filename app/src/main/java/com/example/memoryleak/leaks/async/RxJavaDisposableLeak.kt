package com.example.memoryleak.leaks.async

import android.os.Handler
import android.os.Looper

class RxJavaDisposableLeak {
    private var isSimulatedDisposableActive = false

    fun triggerLeak(): String {
        // LEAK: Subscribing to infinite stream/Observable without storing Disposable & calling dispose().
        isSimulatedDisposableActive = true
        Handler(Looper.getMainLooper()).postDelayed({
            // Active stream event
        }, 60000L)
        return "Subscribed to RxJava / Reactive Stream without storing Disposable or calling dispose()."
    }

    fun triggerFix(): String {
        isSimulatedDisposableActive = false
        return "Fixed! Called disposable.dispose() or clear CompositeDisposable in onDestroy()."
    }
}
