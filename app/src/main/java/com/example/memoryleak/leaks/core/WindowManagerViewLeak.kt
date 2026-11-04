package com.example.memoryleak.leaks.core

import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager

class WindowManagerViewLeak(private val context: Context) {
    private var windowManager: WindowManager? = null
    private var addedView: View? = null

    fun triggerLeak(): String {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val view = View(context)
        addedView = view
        val params = WindowManager.LayoutParams(
            1, 1,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        // LEAK: Adding overlay/floating View via WindowManager without calling windowManager.removeView().
        try {
            windowManager?.addView(view, params)
        } catch (e: Exception) {
            // Simulated add
        }
        return "Added floating View via WindowManager without calling removeViewImmediate()/removeView()."
    }

    fun triggerFix(): String {
        addedView?.let { v ->
            try {
                windowManager?.removeViewImmediate(v)
            } catch (e: Exception) {
                // Ignore
            }
            addedView = null
        }
        return "Fixed! Called windowManager.removeViewImmediate() to detach view."
    }
}
