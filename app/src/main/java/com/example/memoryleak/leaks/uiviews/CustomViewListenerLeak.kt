package com.example.memoryleak.leaks.uiviews

import android.content.Context
import android.util.AttributeSet
import android.view.View

class CustomViewListenerLeak(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    // LEAK: Custom View capturing Activity Context in internal callback listener field without clear method.
    private var customListener: OnCustomEventListener? = null

    interface OnCustomEventListener {
        fun onEvent()
    }

    fun setCustomEventListener(listener: OnCustomEventListener) {
        this.customListener = listener
    }

    fun triggerLeak(): String {
        return "CustomView retaining callback listener referencing outer Activity."
    }

    fun triggerFix(): String {
        this.customListener = null
        return "Fixed! Nullified custom listener reference when View detaches from window."
    }
}
