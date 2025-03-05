package com.tugasmobile.inventory.notifikasi

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tugasmobile.inventory.MainActivity

class NotificationHelper(private val context: Context) {

    private val CHANNEL_ID = "stock_alert_channel"

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stock Alert"
            val descriptionText = "Notifikasi stok kurang"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NOTIF", "Notification channel dibuat: $CHANNEL_ID")
        }
    }

    fun sendNotification(itemName: String, currentStock: Int) {
        Log.d("NOTIF", "Mengirim notifikasi untuk $itemName dengan stok $currentStock")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                Log.d("NOTIF", "Izin notifikasi tidak diberikan")
                // Tidak memiliki izin, tidak bisa mengirim notifikasi
                return
            }
        }
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Stok Barang Rendah!")
            .setContentText("Stok $itemName tersisa $currentStock! Segera restok!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .setContentIntent(pendingIntent)

        val notificationId = itemName.hashCode()
        val notificationManager = NotificationManagerCompat.from(context)
        if (!notificationManager.areNotificationsEnabled()) {
            Log.d("NOTIF", "Notifikasi dinonaktifkan oleh pengguna. Tidak bisa mengirim notifikasi.")
            return
        }

        Log.d("NOTIF", "Mengirim notifikasi untuk $itemName dengan stok $currentStock")
        notificationManager.notify(notificationId, builder.build())
    }
}
