package com.tugasmobile.inventory.ui.setting.notifikasi

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import com.tugasmobile.inventory.database.SyncDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val dbLama = BrgDatabaseHelper.getInstance(applicationContext)
                val dbBaru = SyncDatabaseHelper.getInstance(applicationContext)
                val lowStockItems = dbLama.getLowStockItems()

                for (item in lowStockItems) {
                    dbBaru.insertData(item.kodeBarang, item.stok)
                }
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }
}