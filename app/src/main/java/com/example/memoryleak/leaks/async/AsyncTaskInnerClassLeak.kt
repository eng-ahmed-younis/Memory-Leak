package com.example.memoryleak.leaks.async

class AsyncTaskInnerClassLeak {
    private var isTaskRunning = false

    fun triggerLeak(): String {
        // LEAK: Non-static inner AsyncTask running background doInBackground while capturing outer Activity.
        isTaskRunning = true
        return "Executed background AsyncTask inner class capturing Activity context during config change."
    }

    fun triggerFix(): String {
        isTaskRunning = false
        return "Fixed! Cancelled AsyncTask or migrated to Kotlin Coroutines lifecycleScope."
    }
}
