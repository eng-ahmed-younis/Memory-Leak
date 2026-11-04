package com.example.memoryleak.leaks.uiviews

import android.content.Context
import android.view.View
import android.widget.TextView

object StaticViewLeak {
    // LEAK: Static reference to a View object.
    // Views retain a strong reference to their host Context (Activity), preventing GC from reclaiming the entire Activity tree.
    var leakyView: View? = null

    fun triggerLeak(context: Context): String {
        val view = TextView(context).apply {
            text = "Leaky Static View"
        }
        leakyView = view
        return "Created TextView with Activity Context and saved in static field."
    }

    fun triggerFix(): String {
        leakyView = null
        return "Fixed! Cleared static reference to View."
    }
}
