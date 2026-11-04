package com.example.memoryleak.leaks.singletons

import android.os.Handler
import android.os.Looper

class WebSocketListenerLeak {
    private var isWebSocketConnected = false

    fun triggerLeak(): String {
        // LEAK: Persistent WebSocket connection holding UI listener reference in OkHttpClient background thread.
        isWebSocketConnected = true
        Handler(Looper.getMainLooper()).postDelayed({
            // Incoming socket message
        }, 30000L)
        return "Established persistent WebSocket connection holding Activity listener without socket.close()."
    }

    fun triggerFix(): String {
        isWebSocketConnected = false
        return "Fixed! Called webSocket.close(1000, 'Screen closed') and unlinked listener."
    }
}
