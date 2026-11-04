package com.example.memoryleak.leaks.async

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveDataObserveForeverLeak {
    private val liveData = MutableLiveData<String>()
    private var observer: Observer<String>? = null

    fun triggerLeak(): String {
        val obs = Observer<String> {
            // Processing value
        }
        observer = obs
        // LEAK: LiveData.observeForever() bypasses LifecycleOwner.
        // Failing to call removeObserver keeps observer & host component retained permanently.
        liveData.observeForever(obs)
        return "Called LiveData.observeForever() without calling liveData.removeObserver()."
    }

    fun triggerFix(): String {
        observer?.let {
            liveData.removeObserver(it)
            observer = null
        }
        return "Fixed! Called liveData.removeObserver() or use observe(lifecycleOwner) instead."
    }
}
