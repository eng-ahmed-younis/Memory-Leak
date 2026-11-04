package com.example.memoryleak.leaks.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

object ComposeRememberLeak {
    // LEAK: Using remember { ... } in Jetpack Compose storing Activity context or heavy non-recomposable state across recompositions without clearing.
    @Composable
    fun triggerLeakDemo(context: Context): String {
        val storedContext = remember { context }
        return "Compose remember { ... } stored Activity Context '${storedContext.javaClass.simpleName}'."
    }

    fun getExplanation(): String {
        return "In Jetpack Compose, passing Activity Context inside `remember { context }` can cause state retention or leaks if captured in long-lived state holders or lambdas."
    }
}
