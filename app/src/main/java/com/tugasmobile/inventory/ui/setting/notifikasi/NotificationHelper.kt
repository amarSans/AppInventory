package com.tugasmobile.inventory.ui.setting.notifikasi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.tugasmobile.inventory.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "reminder_channel"
    }
    private val NOTIF_ID = 1001

    fun createNotificationChannel() {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(CHANNEL_ID, "Stok Rendah", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

    }

    fun sendNotification(namaBarang: String, stok: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Stok Rendah: $namaBarang")
            .setContentText("Sisa stok: $stok")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIF_ID, notification)
    }
}