package com.example.memoryleak.leaks.core

import android.content.ClipboardManager
import android.content.Context

class ClipboardListenerLeak(private val context: Context) {
    private var clipboardManager: ClipboardManager? = null
    private var listener: ClipboardManager.OnPrimaryClipChangedListener? = null

    fun triggerLeak(): String {
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
            // Clipboard changed
        }
        listener = clipListener
        // LEAK: Adding OnPrimaryClipChangedListener without calling removePrimaryClipChangedListener.
        clipboardManager?.addPrimaryClipChangedListener(clipListener)
        return "Added OnPrimaryClipChangedListener to ClipboardManager without calling removePrimaryClipChangedListener()."
    }

    fun triggerFix(): String {
        listener?.let {
            clipboardManager?.removePrimaryClipChangedListener(it)
            listener = null
        }
        return "Fixed! Removed OnPrimaryClipChangedListener from ClipboardManager."
    }
}
