package com.example.alarmclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Stop the current alarm sound
        AlarmReceiver.stopRingtone()

        // Handle snooze action: set the alarm to go off after the snooze duration
        val snoozeTimeMillis = 10 * 60 * 1000 // 10 minutes in milliseconds
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + snoozeTimeMillis,
            alarmIntent
        )

        // Cancel the current notification
        with(NotificationManagerCompat.from(context)) {
            cancel(1)
        }
    }
}
