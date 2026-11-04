package com.example.memoryleak.permission

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * PROBLEM EXPLANATION:
 * Using `suspendCoroutine` to wrap runtime permission requests (`ActivityResultLauncher`) causes memory leaks during screen rotation.
 * The Kotlin compiler generates a `Continuation` object to hold the execution state. The lambda closure inside `suspendCoroutine`
 * captures a strong reference to the outer helper and `ActivityResultLauncher`, which is tied directly to the host `Activity`.
 * When a rotation happens while the permission dialog is displayed, the active `Continuation` retains the dead `Activity` instance in heap memory.
 * Additionally, attempting to re-launch `launcher.launch()` in `onCreate()` on rotation causes an infinite dialog spawning bug.
 *
 * SOLUTION EXPLANATION:
 * 1. Decouple state handling from the `Activity` using [PermissionBridge] (a process-wide singleton with a `Channel`/`Flow`).
 *    The bridge stores no `Activity` or `Context` references, preventing memory leaks.
 * 2. Manage the UI attachment layer with [RotatablePermissionHelper], which attaches to the `Activity`'s lifecycle.
 *    It registers the launcher in `onCreate()` and nullifies references in `onDestroy()`.
 * 3. Use `.first()` on `permissionResults` flow to sequentially await results safely across rotation boundaries.
 */
object PermissionBridge {
    // Use a Channel to pass sequential permission results without retaining Activity references
    private val resultChannel = Channel<Boolean>(Channel.CONFLATED)
    val permissionResults = resultChannel.receiveAsFlow()

    // Track whether a permission request dialog is currently active in the system UI
    var isRequestActive: Boolean = false
        private set

    fun markRequestStarted() {
        isRequestActive = true
    }

    fun sendResult(isGranted: Boolean) {
        isRequestActive = false
        resultChannel.trySend(isGranted)
    }
}
