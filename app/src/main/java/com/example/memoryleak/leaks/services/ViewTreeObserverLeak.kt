package com.example.memoryleak.leaks.services

import android.view.View
import android.view.ViewTreeObserver

class ViewTreeObserverLeak {
    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var targetView: View? = null

    fun triggerLeak(view: View): String {
        targetView = view
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            // Layout logic
        }
        layoutListener = listener
        // LEAK: Adding OnGlobalLayoutListener to ViewTreeObserver without removing it.
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        return "Added OnGlobalLayoutListener to ViewTreeObserver without calling removeOnGlobalLayoutListener()."
    }

    fun triggerFix(): String {
        layoutListener?.let { listener ->
            targetView?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
            layoutListener = null
            targetView = null
        }
        return "Fixed! Called removeOnGlobalLayoutListener() when layout listener was no longer needed."
    }
}
