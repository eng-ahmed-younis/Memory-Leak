package com.example.memoryleak.leaks.core

import android.view.View

class FragmentViewBindingLeak {
    // LEAK: Storing a strong reference to Fragment View or ViewBinding across onDestroyView().
    // Fragments outlive their View hierarchy in backstack, causing massive layout memory leaks.
    private var leakyViewReference: View? = null

    fun triggerLeak(rootView: View): String {
        leakyViewReference = rootView
        return "Leaked View reference retained beyond Fragment view destruction."
    }

    fun triggerFix(): String {
        // FIX: Set view reference / binding to null in onDestroyView().
        leakyViewReference = null
        return "Fixed! Nullified view binding reference in onDestroyView()."
    }
}
