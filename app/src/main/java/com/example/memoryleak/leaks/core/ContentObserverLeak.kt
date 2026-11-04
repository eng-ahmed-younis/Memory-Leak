package com.example.memoryleak.leaks.core

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings

class ContentObserverLeak(private val context: Context) {
    private var observer: ContentObserver? = null

    fun triggerLeak(): String {
        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
            }
        }
        observer = contentObserver
        // LEAK: Registering ContentObserver on ContentResolver without unregistering on destroy.
        context.contentResolver.registerContentObserver(
            Settings.System.CONTENT_URI,
            true,
            contentObserver
        )
        return "Registered ContentObserver on ContentResolver without calling unregisterContentObserver."
    }

    fun triggerFix(): String {
        observer?.let {
            context.contentResolver.unregisterContentObserver(it)
            observer = null
        }
        return "Fixed! Unregistered ContentObserver from ContentResolver."
    }
}
