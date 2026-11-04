package com.example.memoryleak.leaks.async

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineGlobalScopeLeak {
    private var activeJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun triggerLeak(): String {
        // LEAK: GlobalScope.launch runs independently of Activity/Fragment lifecycle.
        // It captures references to the outer class and keeps them alive for long-running delays/loops.
        activeJob = GlobalScope.launch {
            while (true) {
                delay(10000L)
                // Doing background work holding outer reference
            }
        }
        return "Launched long-running coroutine in GlobalScope capture outer context without cancellation."
    }

    fun triggerFix(): String {
        // FIX: Cancel coroutine scope/job when component lifecycle is destroyed (or use lifecycleScope / viewModelScope).
        activeJob?.cancel()
        activeJob = null
        return "Fixed! Canceled Job when lifecycle ended (use lifecycleScope / viewModelScope instead)."
    }
}
