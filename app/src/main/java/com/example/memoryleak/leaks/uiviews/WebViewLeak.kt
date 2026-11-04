package com.example.memoryleak.leaks.uiviews

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView

class WebViewLeak(private val context: Context) {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun triggerLeak(): String {
        // LEAK: WebView initialized with Activity context retains internal references to Context listeners.
        // If not destroyed cleanly in onDestroy, it prevents Activity GC.
        val wv = WebView(context).apply {
            settings.javaScriptEnabled = true
        }
        webView = wv
        return "Created WebView with Activity Context without invoking webView.destroy()."
    }

    fun triggerFix(): String {
        webView?.let { wv ->
            (wv.parent as? ViewGroup)?.removeView(wv)
            wv.stopLoading()
            wv.clearHistory()
            wv.removeAllViews()
            wv.destroy()
            webView = null
        }
        return "Fixed! Removed WebView from parent layout, cleared history and called webView.destroy()."
    }
}
