package com.example.memoryleak.leaks.core

import android.content.Context
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager

class CameraHardwareLeak(private val context: Context) {
    private var activeCameraDevice: CameraDevice? = null

    fun triggerLeak(): String {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        // LEAK: Opening low-level camera device without calling cameraDevice.close() in onPause()/onDestroy().
        try {
            val cameraId = cameraManager?.cameraIdList?.firstOrNull()
            if (cameraId != null) {
                // Simulated camera open
            }
        } catch (e: Exception) {
            // Simulated open
        }
        return "Opened native Camera hardware session without calling cameraDevice.close()."
    }

    fun triggerFix(): String {
        activeCameraDevice?.close()
        activeCameraDevice = null
        return "Fixed! Called cameraDevice.close() to release camera hardware subsystem."
    }
}
