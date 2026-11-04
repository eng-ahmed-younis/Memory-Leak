package com.example.memoryleak.leaks.core

import android.app.Activity

object ActivityRegistryLeak {
    // LEAK: Static list or global registry retaining destroyed Activity instances.
    // When activities are created, they are added to the list, but never removed on destroy.
    private val activityRegistry = mutableListOf<Activity>()

    fun triggerLeak(activity: Activity): String {
        activityRegistry.add(activity)
        return "Added Activity instance '${activity.javaClass.simpleName}' to static registry (Registry count: ${activityRegistry.size})."
    }

    fun triggerFix(activity: Activity): String {
        activityRegistry.remove(activity)
        return "Fixed! Removed Activity instance from static registry."
    }
}
