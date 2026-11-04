package com.example.memoryleak.leaks.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FlowListenerLeak {
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()
    private var collectionJob: Job? = null

    fun triggerLeak(): String {
        // LEAK: Collecting a hot StateFlow/SharedFlow in a custom unmanaged CoroutineScope without repeatOnLifecycle or job cancellation.
        val scope = CoroutineScope(Dispatchers.Main)
        collectionJob = scope.launch {
            eventFlow.collect {
                // Handle event
            }
        }
        return "Started collecting hot SharedFlow in unmanaged CoroutineScope without cancellation."
    }

    fun triggerFix(): String {
        // FIX: Cancel collection job or collect with lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED).
        collectionJob?.cancel()
        collectionJob = null
        return "Fixed! Canceled flow collection Job or use repeatOnLifecycle in Compose/Fragment."
    }
}
