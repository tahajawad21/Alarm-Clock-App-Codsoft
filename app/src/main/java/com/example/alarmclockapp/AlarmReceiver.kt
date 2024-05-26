package com.example.alarmclockapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private var ringtone: Ringtone? = null

        fun stopRingtone() {
            ringtone?.stop()
            ringtone = null
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val tone = intent.getStringExtra("ALARM_TONE")
        val alarmUri: Uri = when (tone) {
            "Alarm Tone 1" -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            "Alarm Tone 2" -> Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_tone_2)
            "Alarm Tone 3" -> Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarm_tone_3)
            else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, alarmUri)
        }
        ringtone?.play()

        createNotificationChannel(context)
        showNotification(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Channel for Alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context) {
        val snoozeIntent = Intent(context, SnoozeReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val dismissIntent = Intent(context, DismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Alarm")
            .setContentText("Your alarm is ringing")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent)
            .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
