package com.example.memoryleak.leaks.uiviews

import android.view.View

class ViewPagerFragmentLeak {
    private val cachedViews = mutableListOf<View>()

    fun triggerLeak(view: View): String {
        // LEAK: ViewPager adapter keeping strong references to instantiated Fragment views in static or cached list.
        cachedViews.add(view)
        return "Retained Fragment View instance in ViewPager adapter cache list (Count: ${cachedViews.size})."
    }

    fun triggerFix(): String {
        cachedViews.clear()
        return "Fixed! Cleared ViewPager adapter view cache on destroy."
    }
}
