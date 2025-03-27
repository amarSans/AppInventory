package com.tugasmobile.inventory.ui.main.setting.notifikasi

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object AlarmScheduler {
    private const val TAG = "AlarmScheduler"

    private const val PREFS_NAME = "NotificationPrefs"
    private const val KEY_LAST_NOTIFICATION_TIME = "last_notification_time"
    fun scheduleNotification(context: Context) {
        val databaseHelper = BrgDatabaseHelper(context)
        val jadwal = databaseHelper.getSetting()
        if (jadwal != null && jadwal.isNotifEnabled) {
            Log.d(
                TAG,
                "Data dari database: startDay=${jadwal.startDay}, endDay=${jadwal.endDay}, notifTime=${jadwal.notifTime}, isNotifEnabled=${jadwal.isNotifEnabled}"
            )

            val hariMulai = jadwal.startDay // Contoh: "Senin"
            val hariBerakhir = jadwal.endDay // Contoh: "Selasa"

            val hariMulaiIndex = convertDayToIndex(hariMulai)
            val hariBerakhirIndex = convertDayToIndex(hariBerakhir)
            val hariIniIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            Log.d(
                TAG,
                "Konversi hari: hariMulai=$hariMulaiIndex, hariBerakhir=$hariBerakhirIndex, hariIni=$hariIniIndex"
            )

            // Cek apakah hari ini masih dalam rentang
            if (isWithinSchedule(hariIniIndex, hariMulaiIndex, hariBerakhirIndex)) {
                val waktuMillis = getNextNotificationTime(jadwal.notifTime)
                Log.d(TAG, "Notifikasi dijadwalkan pada: ${formatMillisToTime(waktuMillis)}")
                setAlarm(context, waktuMillis)
                if (!isNotificationShownToday(context)) {
                    setAlarm(context, waktuMillis)
                } else {
                    Log.d(TAG, "Notifikasi sudah muncul hari ini, tidak menjadwalkan ulang")
                }
            } else {
                Log.d(TAG, "Hari ini di luar rentang, notifikasi dibatalkan")
                cancelNotification(context)
            }


        }
    }
    fun resetNotificationStatus(context: Context) {
        val prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("last_notification_time") // Hapus waktu terakhir notifikasi muncul
        editor.apply()
        Log.d(TAG, "Status notifikasi di-reset")
    }
    private fun isNotificationShownToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastNotificationTime = prefs.getLong(KEY_LAST_NOTIFICATION_TIME, 0)

        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = lastNotificationTime
        val lastNotificationDay = calendar.get(Calendar.DAY_OF_YEAR)

        return today == lastNotificationDay
    }

    private fun setAlarm(context: Context, waktuMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, waktuMillis, pendingIntent)
        Log.d(TAG, "Alarm telah disetel untuk: ${formatMillisToTime(waktuMillis)}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Izin alarm tidak tersedia, meminta izin ke pengguna")
                Toast.makeText(
                    context,
                    "Izin diperlukan untuk menjadwalkan notifikasi tepat waktu",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            }
        }
    }

    fun cancelNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Notifikasi dibatalkan")
    }

    fun rescheduleNotification(context: Context) {
        Log.d(TAG, "Menjadwalkan ulang notifikasi...")
        scheduleNotification(context)
    }

    private fun getNextNotificationTime(waktuMulai: String): Long {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val time = sdf.parse(waktuMulai)
        if (time != null) {
            val currentCalendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, time.hours)
            calendar.set(Calendar.MINUTE, time.minutes)
            calendar.set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun convertDayToIndex(day: String): Int {
        return when (day.lowercase(Locale.getDefault())) {
            "minggu" -> Calendar.SUNDAY
            "senin" -> Calendar.MONDAY
            "selasa" -> Calendar.TUESDAY
            "rabu" -> Calendar.WEDNESDAY
            "kamis" -> Calendar.THURSDAY
            "jumat" -> Calendar.FRIDAY
            "sabtu" -> Calendar.SATURDAY
            else -> -1 // Invalid
        }
    }

    private fun isWithinSchedule(today: Int, start: Int, end: Int): Boolean {
        return if (start <= end) {
            today in start..end
        } else {
            today >= start || today <= end
        }
    }

    private fun formatMillisToTime(millis: Long): String {
        val sdf = SimpleDateFormat("EEEE, HH:mm:ss", Locale.getDefault())
        return sdf.format(millis)
    }
}
