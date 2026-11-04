package com.example.memoryleak.leaks.singletons

import android.graphics.Bitmap

object BitmapCacheLeak {
    // LEAK: Static cache holding heavy Bitmaps without size limits or eviction policy.
    // Keeps large byte arrays in heap memory indefinitely.
    private val staticImageCache = mutableMapOf<String, Bitmap>()

    fun triggerLeak(id: String): String {
        // Create 2000x2000 bitmap (~16MB memory)
        val bitmap = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888)
        staticImageCache[id] = bitmap
        return "Added 16MB Bitmap to unbounded static map cache (Cache size: ${staticImageCache.size} items)."
    }

    fun triggerFix(): String {
        // FIX: Recycle bitmaps and clear cache when no longer needed or use LruCache.
        staticImageCache.values.forEach {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
        staticImageCache.clear()
        return "Fixed! Recycled Bitmaps and cleared static cache."
    }
}
