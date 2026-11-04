package com.example.memoryleak.leaks.singletons

import android.content.Context

class SingletonContextLeak private constructor(context: Context) {
    // LEAK: Holding Activity context inside a Singleton instance.
    // Singleton lives as long as the Application process, causing the stored Activity to leak forever.
    private val storedContext: Context = context

    companion object {
        @Volatile
        private var instance: SingletonContextLeak? = null

        fun triggerLeak(context: Context): String {
            instance = SingletonContextLeak(context)
            return "Singleton initialized holding reference to Context '${context.javaClass.simpleName}'."
        }

        fun triggerFix(context: Context): String {
            // FIX: Use applicationContext for Singletons instead of Activity context!
            instance = SingletonContextLeak(context.applicationContext)
            return "Fixed! Initialized Singleton with applicationContext ('${context.applicationContext.javaClass.simpleName}')."
        }
    }
}
