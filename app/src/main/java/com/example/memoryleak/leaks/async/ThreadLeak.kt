package com.example.memoryleak.leaks.async

class ThreadLeak {
    private var leakyThread: Thread? = null

    fun triggerLeak(): String {
        // LEAK: Anonymous inner class Thread or unmanaged background task.
        // It holds implicit reference to outer class instance and runs indefinitely.
        val thread = object : Thread("LeakyThread") {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(5000L)
                    }
                } catch (e: InterruptedException) {
                    // Thread interrupted
                }
            }
        }
        leakyThread = thread
        thread.start()
        return "Started long-running background Thread retaining implicit reference to host component."
    }

    fun triggerFix(): String {
        leakyThread?.let {
            it.interrupt()
            leakyThread = null
        }
        return "Fixed! Interrupted background Thread and cleared reference."
    }
}
