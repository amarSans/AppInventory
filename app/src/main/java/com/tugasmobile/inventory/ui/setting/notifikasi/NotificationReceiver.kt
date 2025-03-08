package com.tugasmobile.inventory.ui.setting.notifikasi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("NOTIF_RECEIVER", "Broadcast diterima, memicu notifikasi")
        if (context != null) {
            val itemName = intent?.getStringExtra("ITEM_NAME") ?: "Barang"
            val currentStock = intent?.getIntExtra("CURRENT_STOCK", 0) ?: 0

            val notificationHelper = NotificationHelper(context)
            notificationHelper.createNotificationChannel() // Pastikan channel dibuat
            notificationHelper.sendNotification(itemName, currentStock)
            saveLastNotificationTime(context)
        }

    }
    private fun saveLastNotificationTime(context: Context) {
        val prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong("last_notification_time", System.currentTimeMillis())
        editor.apply() // Gunakan apply() untuk menyimpan secara asinkron
    }

}
