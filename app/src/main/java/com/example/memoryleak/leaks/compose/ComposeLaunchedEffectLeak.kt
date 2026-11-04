package com.example.memoryleak.leaks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

object ComposeLaunchedEffectLeak {
    @Composable
    fun triggerLeakDemo(): String {
        // LEAK: LaunchedEffect with key(Unit) capturing outer state/context that should be updated across recompositions.
        LaunchedEffect(Unit) {
            while (true) {
                delay(10000L)
            }
        }
        return "LaunchedEffect(Unit) started infinite coroutine loop capturing non-remembered context."
    }

    fun getExplanation(): String {
        return "In Jetpack Compose, ensure LaunchedEffect keys match changing parameters, or use rememberUpdatedState to prevent state capture leaks."
    }
}
