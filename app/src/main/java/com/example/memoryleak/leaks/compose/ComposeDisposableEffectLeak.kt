package com.example.memoryleak.leaks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

object ComposeDisposableEffectLeak {
    // LEAK: DisposableEffect registering a listener but having an empty or missing onDispose cleanup block!
    @Composable
    fun triggerLeakDemo(): String {
        DisposableEffect(Unit) {
            // Register listener...
            onDispose {
                // LEAK: Leaving cleanup empty or forgetting to unregister listener!
            }
        }
        return "DisposableEffect registered listener with empty onDispose { } cleanup block!"
    }

    fun getExplanation(): String {
        return "In Compose, DisposableEffect MUST unregister subscriptions, broadcast receivers, or listeners inside its onDispose block when leaving composition."
    }
}
