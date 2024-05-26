package com.example.alarmclockapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationManagerCompat

class DismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Stop the current alarm sound
        AlarmReceiver.stopRingtone()

        // Cancel the current notification
        with(NotificationManagerCompat.from(context)) {
            cancel(1)
        }
    }
}
