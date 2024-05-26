package com.example.alarmclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

data class Alarm(
    val hour: Int,
    val minute: Int,
    val time: String,
    var isEnabled: Boolean,
    val tone: String
)

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private val alarms = mutableListOf<Alarm>()

    fun addAlarm(alarm: Alarm) {
        alarms.add(alarm)
        notifyItemInserted(alarms.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount(): Int = alarms.size

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val alarmTimeTextView: TextView = itemView.findViewById(R.id.alarmTime)
        private val alarmToggleButton: ToggleButton = itemView.findViewById(R.id.alarmToggle)

        fun bind(alarm: Alarm) {
            alarmTimeTextView.text = alarm.time
            alarmToggleButton.isChecked = alarm.isEnabled
            alarmToggleButton.setOnCheckedChangeListener { _, isChecked ->
                alarm.isEnabled = isChecked
                // Enable or disable the alarm
                if (isChecked) {
                    setAlarm(alarm.hour, alarm.minute, alarm.tone)
                } else {
                    cancelAlarm(alarm.hour, alarm.minute)
                }
            }
        }

        private fun setAlarm(hour: Int, minute: Int, tone: String) {
            val context = itemView.context
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                intent.putExtra("ALARM_TONE", tone)
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        }

        private fun cancelAlarm(hour: Int, minute: Int) {
            val context = itemView.context
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            alarmManager.cancel(alarmIntent)
        }
    }
}
