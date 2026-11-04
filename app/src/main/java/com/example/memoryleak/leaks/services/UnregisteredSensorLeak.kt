package com.example.memoryleak.leaks.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class UnregisteredSensorLeak(private val context: Context) {
    private var sensorListener: SensorEventListener? = null
    private var sensorManager: SensorManager? = null

    fun triggerLeak(): String {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {}
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorListener = listener
        // LEAK: Registering sensor listener holding implicit outer context without unregistering on stop/destroy.
        accelerometer?.let {
            sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        return "Registered SensorEventListener with SensorManager without calling unregisterListener()."
    }

    fun triggerFix(): String {
        sensorListener?.let {
            sensorManager?.unregisterListener(it)
            sensorListener = null
        }
        return "Fixed! Unregistered SensorEventListener cleanly when component was stopped."
    }
}
