package com.example.memoryleak.leaks.uiviews

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class TextWatcherLeak {
    private var textWatcher: TextWatcher? = null
    private var targetEditText: EditText? = null

    fun triggerLeak(editText: EditText): String {
        targetEditText = editText
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher = watcher
        // LEAK: Adding TextWatcher to a long-lived or shared view/singleton without calling removeTextChangedListener.
        editText.addTextChangedListener(watcher)
        return "Added TextWatcher inner class to EditText without calling removeTextChangedListener()."
    }

    fun triggerFix(): String {
        textWatcher?.let { watcher ->
            targetEditText?.removeTextChangedListener(watcher)
            textWatcher = null
            targetEditText = null
        }
        return "Fixed! Removed TextWatcher via removeTextChangedListener()."
    }
}
