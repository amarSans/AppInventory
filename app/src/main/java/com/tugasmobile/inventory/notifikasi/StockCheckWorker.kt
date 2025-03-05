package com.tugasmobile.inventory.notifikasi
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.tugasmobile.inventory.data.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockCheckWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("StockCheckWorker", "Memulai pengecekan stok...")
                val databaseHelper = BrgDatabaseHelper.getInstance(applicationContext)
                val lowStockItems = databaseHelper.getLowStockItems() // Ambil barang dengan stok rendah

                if (lowStockItems.isNotEmpty()) {
                    val notificationHelper = NotificationHelper(applicationContext)
                    notificationHelper.createNotificationChannel()
                    for (item in lowStockItems) {
                        Log.d("StockCheckWorker", "Mengirim notifikasi untuk: ${item.namaBarang} (${item.stok})")
                        notificationHelper.sendNotification(item.namaBarang, item.stok)
                    }
                }else {
                    Log.d("StockCheckWorker", "Tidak ada barang dengan stok rendah")
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("StockCheckWorker", "Error dalam pengecekan stok", e)
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}