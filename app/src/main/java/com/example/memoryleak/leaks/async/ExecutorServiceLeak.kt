package com.example.memoryleak.leaks.async

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExecutorServiceLeak {
    private var executorService: ExecutorService? = null

    fun triggerLeak(): String {
        // LEAK: Creating ThreadPool / ExecutorService in Activity/Fragment without calling executor.shutdown().
        // Worker threads remain alive indefinitely in JVM pool holding captured references.
        val executor = Executors.newFixedThreadPool(4)
        executorService = executor
        executor.execute {
            try {
                Thread.sleep(60000L)
            } catch (e: InterruptedException) {
                // Interrupted
            }
        }
        return "Instantiated ExecutorService thread pool without calling executor.shutdown()."
    }

    fun triggerFix(): String {
        executorService?.let {
            it.shutdownNow()
            executorService = null
        }
        return "Fixed! Called executor.shutdownNow() to terminate thread pool."
    }
}
