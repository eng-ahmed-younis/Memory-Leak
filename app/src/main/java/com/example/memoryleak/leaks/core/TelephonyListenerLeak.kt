package com.example.memoryleak.leaks.core

import android.content.Context
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class TelephonyListenerLeak(private val context: Context) {
    private var telephonyManager: TelephonyManager? = null
    private var phoneStateListener: PhoneStateListener? = null

    @Suppress("DEPRECATION")
    fun triggerLeak(): String {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val listener = object : PhoneStateListener() {
            @Deprecated("Deprecated in API")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {}
        }
        phoneStateListener = listener
        // LEAK: Registering PhoneStateListener on TelephonyManager without unregistering (LISTEN_NONE).
        try {
            telephonyManager?.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            // Simulated call
        }
        return "Registered PhoneStateListener on TelephonyManager without resetting to LISTEN_NONE."
    }

    @Suppress("DEPRECATION")
    fun triggerFix(): String {
        phoneStateListener?.let {
            try {
                telephonyManager?.listen(it, PhoneStateListener.LISTEN_NONE)
            } catch (e: Exception) {
                // Ignore
            }
            phoneStateListener = null
        }
        return "Fixed! Unregistered PhoneStateListener by passing PhoneStateListener.LISTEN_NONE."
    }
}
