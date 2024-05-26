package com.example.alarmclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var currentTimeTextView: TextView
    private lateinit var currentDateTextView: TextView
    private lateinit var setAlarmButton: Button
    private lateinit var alarmList: RecyclerView

    private val alarmAdapter = AlarmAdapter()
    private val handler = Handler(Looper.getMainLooper())
    private val timeUpdater = object : Runnable {
        override fun run() {
            updateTimeAndDate()
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentTimeTextView = findViewById(R.id.currentTime)
        currentDateTextView = findViewById(R.id.currentDate)
        setAlarmButton = findViewById(R.id.setAlarmButton)
        alarmList = findViewById(R.id.alarmList)

        alarmList.layoutManager = LinearLayoutManager(this)
        alarmList.adapter = alarmAdapter

        // Set text color programmatically if needed
        val greyColor = ContextCompat.getColor(this, R.color.grey)
        currentTimeTextView.setTextColor(greyColor)
        currentDateTextView.setTextColor(greyColor)

        setAlarmButton.setOnClickListener {
            showTimePickerDialog()
        }

        handler.post(timeUpdater) // Start updating the time
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeUpdater) // Stop updating the time
    }

    private fun updateTimeAndDate() {
        val currentTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
        val currentDate = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(Date())
        currentTimeTextView.text = currentTime
        currentDateTextView.text = currentDate
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            showAlarmSettingDialog(selectedHour, selectedMinute)
        }, hour, minute, DateFormat.is24HourFormat(this))

        timePickerDialog.show()
    }

    private fun showAlarmSettingDialog(hour: Int, minute: Int) {
        val dialogView = layoutInflater.inflate(R.layout.alarm_setting_dialog, null)
        val timePicker: TimePicker = dialogView.findViewById(R.id.timePicker)
        val alarmToneSpinner: Spinner = dialogView.findViewById(R.id.alarmToneSpinner)

        timePicker.hour = hour
        timePicker.minute = minute

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute
                val selectedTone = alarmToneSpinner.selectedItem.toString()
                val newAlarmTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                val alarm = Alarm(selectedHour, selectedMinute, newAlarmTime, true, selectedTone)
                alarmAdapter.addAlarm(alarm)
                setAlarm(selectedHour, selectedMinute, selectedTone)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setAlarm(hour: Int, minute: Int, tone: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("ALARM_TONE", tone)
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        Toast.makeText(this, "Alarm set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.cancel(alarmIntent)
        Toast.makeText(this, "Alarm canceled for $hour:$minute", Toast.LENGTH_SHORT).show()
    }
}
