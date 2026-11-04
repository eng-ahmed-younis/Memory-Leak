package com.example.memoryleak.leaks.core

import android.content.Context
import android.speech.SpeechRecognizer

class SpeechRecognizerLeak(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun triggerLeak(): String {
        // LEAK: Creating SpeechRecognizer using Context without calling destroy().
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
        return "Instantiated SpeechRecognizer using Context without calling speechRecognizer.destroy()."
    }

    fun triggerFix(): String {
        speechRecognizer?.let {
            it.destroy()
            speechRecognizer = null
        }
        return "Fixed! Destroyed SpeechRecognizer native resources."
    }
}
