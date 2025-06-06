package com.muammar.inventory.ui.setting.notifikasi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.muammar.inventory.ui.main.MainActivity
import com.muammar.inventory.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "reminder_channel"
    }
    private val NOTIF_ID = 1001

    fun createNotificationChannel() {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(CHANNEL_ID, "Pemberitahuan Stok", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

    }

    fun sendNotification( stok: Int) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("filter_stock", "stok_rendah")
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Pemberitahuan Stok Rendah")
            .setContentText("anda memiliki $stok barang yang perlu diisi")
            .setSmallIcon(R.drawable.baseline_warning_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIF_ID, notification)
    }
}