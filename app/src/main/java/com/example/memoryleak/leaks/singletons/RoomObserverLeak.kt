package com.example.memoryleak.leaks.singletons

class RoomObserverLeak {
    private var isRoomObserverActive = false

    fun triggerLeak(): String {
        // LEAK: Registering InvalidationTracker.Observer with Room database singleton without calling removeObserver.
        isRoomObserverActive = true
        return "Registered InvalidationTracker.Observer with Room Database without calling removeObserver()."
    }

    fun triggerFix(): String {
        isRoomObserverActive = false
        return "Fixed! Removed Room InvalidationTracker.Observer in onDestroy()."
    }
}
