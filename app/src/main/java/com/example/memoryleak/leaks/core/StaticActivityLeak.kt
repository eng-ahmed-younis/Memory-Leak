package com.example.memoryleak.leaks.core

import android.app.Activity
import android.content.Context

object StaticActivityLeak {
    // LEAK: Static reference directly storing an Activity or Context instance.
    // When the Activity is recreated (e.g. rotation) or destroyed, GC cannot collect it.
    var leakedActivity: Activity? = null

    fun triggerLeak(activity: Activity): String {
        leakedActivity = activity
        return "Leaked reference to Activity '${activity.javaClass.simpleName}' stored in companion/object."
    }

    fun triggerFix(activity: Activity): String {
        // FIX: Clear static reference or use WeakReference/ApplicationContext.
        leakedActivity = null
        val appContext: Context = activity.applicationContext
        return "Fixed! Saved Application Context '${appContext.javaClass.simpleName}' instead of Activity, and cleared static reference."
    }
}
