package com.example.memoryleak.leaks.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmManagerLeak(private val context: Context) {
    private var pendingIntent: PendingIntent? = null

    fun triggerLeak(): String {
        val intent = Intent().apply {
            setClassName(context.packageName, "com.example.memoryleak.DummyAlarmReceiver")
        }
        // LEAK: PendingIntent retaining Activity context registered with system AlarmManager and never canceled.
        pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        pendingIntent?.let { pi ->
            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60000,
                60000,
                pi
            )
        }
        return "Registered repeating AlarmManager PendingIntent holding Activity context without calling alarmManager.cancel()."
    }

    fun triggerFix(): String {
        pendingIntent?.let { pi ->
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pi)
            pi.cancel()
            pendingIntent = null
        }
        return "Fixed! Canceled PendingIntent and removed repeating alarm from AlarmManager."
    }
}
