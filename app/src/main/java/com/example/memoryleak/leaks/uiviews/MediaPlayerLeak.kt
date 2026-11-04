package com.example.memoryleak.leaks.uiviews

import android.content.Context
import android.media.MediaPlayer

class MediaPlayerLeak(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun triggerLeak(): String {
        // LEAK: Creating MediaPlayer with Activity context and failing to release native audio resources on exit.
        val player = MediaPlayer()
        mediaPlayer = player
        return "Instantiated MediaPlayer using Context without calling mediaPlayer.release()."
    }

    fun triggerFix(): String {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
            mediaPlayer = null
        }
        return "Fixed! Stopped and released native MediaPlayer resources."
    }
}
