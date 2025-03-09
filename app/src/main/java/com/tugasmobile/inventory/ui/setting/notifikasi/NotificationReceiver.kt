package com.tugasmobile.inventory.ui.setting.notifikasi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("NOTIF_RECEIVER", "Broadcast diterima, memicu notifikasi")
        if (context != null) {
            val lowStockCount = getLowStockCountFromSharedPreferences(context)
            val notificationHelper = NotificationHelper(context)
            notificationHelper.createNotificationChannel() // Pastikan channel dibuat
            notificationHelper.sendNotification(lowStockCount)
            saveLastNotificationTime(context)
        }

    }
    private fun getLowStockCountFromSharedPreferences(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("StokPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("low_stock_count", 0)
    }
    private fun saveLastNotificationTime(context: Context) {
        val prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putLong("last_notification_time", System.currentTimeMillis())
        editor.apply() // Gunakan apply() untuk menyimpan secara asinkron
    }

}
