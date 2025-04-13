package com.tugasmobile.inventory.ui.setting.transferData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.COLUMN_GAMBAR
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_BARANG
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_BARANG_KELUAR
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_BARANG_MASUK
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_HISTORY
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_STOK
import java.io.File

fun exportData(context: Context, database: SQLiteDatabase) {
    val backupDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "BackupInventaTa"
    )
    val imageDir = File(backupDir, "InventoryApp")
    if (!imageDir.exists()) imageDir.mkdirs()

    val allData = mutableMapOf<String, Any>()

    try {
        // 1. Export Tabel Barang
        val itemList = mutableListOf<ItemBarang>()
        val cursorBarang = database.rawQuery("SELECT * FROM $TABLE_BARANG", null)
        while (cursorBarang.moveToNext()) {
            val gambarPath = cursorBarang.getString(cursorBarang.getColumnIndexOrThrow(COLUMN_GAMBAR)) ?: ""

            var newImagePath = ""
            if (gambarPath.isNotEmpty()) {
                val uri = Uri.parse(gambarPath)
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = "img_${System.currentTimeMillis()}.jpg"
                val destFile = File(imageDir, fileName)

                inputStream?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                newImagePath = destFile.absolutePath
            }

            itemList.add(
                ItemBarang(
                    id_barang = cursorBarang.getString(0),
                    merek_barang = cursorBarang.getString(1),
                    karakteristik = cursorBarang.getString(2),
                    gambar = newImagePath,
                    lastUpdate = cursorBarang.getString(4)
                )
            )
        }
        cursorBarang.close()
        allData["barang"] = itemList

        // 2. Tabel Stok
        val stokList = mutableListOf<Stok>()
        val cursorStok = database.rawQuery("SELECT * FROM $TABLE_STOK", null)
        while (cursorStok.moveToNext()) {

            stokList.add(
                Stok(
                    idStok = cursorStok.getLong(0),
                    id_barang = cursorStok.getString(1),
                    ukuranwarna = cursorStok.getString(2),
                    stokBarang = cursorStok.getInt(3)
                )
            )
        }
        cursorStok.close()
        allData["stok"] = stokList

        // 3. Barang Masuk
        val masukList = mutableListOf<BarangIn>()
        val cursorMasuk = database.rawQuery("SELECT * FROM $TABLE_BARANG_MASUK", null)
        while (cursorMasuk.moveToNext()) {
            masukList.add(
                BarangIn(
                    IdBrgMasuk = cursorMasuk.getLong(0),
                    id_barang = cursorMasuk.getString(1),
                    Tgl_Masuk = cursorMasuk.getString(2),
                    Harga_Modal = cursorMasuk.getInt(3),
                    Nama_Toko = cursorMasuk.getString(4) ?: ""
                )
            )
        }
        cursorMasuk.close()
        allData["barang_masuk"] = masukList

        // 4. Barang Keluar
        val keluarList = mutableListOf<BarangOut>()
        val cursorKeluar = database.rawQuery("SELECT * FROM $TABLE_BARANG_KELUAR", null)
        while (cursorKeluar.moveToNext()) {
            keluarList.add(
                BarangOut(
                    IdBrgKeluar = cursorKeluar.getLong(0),
                    id_barang = cursorKeluar.getString(1),
                    ukuran_warna = cursorKeluar.getString(2),
                    stok_keluar = cursorKeluar.getInt(3),
                    Tgl_Keluar = cursorKeluar.getString(4),
                    Hrg_Beli = cursorKeluar.getInt(5)
                )
            )
        }
        cursorKeluar.close()
        allData["barang_keluar"] = keluarList

        // 5. History / Story
        val historyList = mutableListOf<History>()
        val cursorHistory = database.rawQuery("SELECT * FROM $TABLE_HISTORY", null)
        while (cursorHistory.moveToNext()) {
            historyList.add(
                History(
                    id =  cursorHistory.getInt(0),
                    waktu =  cursorHistory.getString(1),
                    kodeBarang = cursorHistory.getString(2),
                    stok =  cursorHistory.getString(3),
                    jenisData =  cursorHistory.getString(4)
                )
            )
        }
        cursorHistory.close()
        allData["story"] = historyList

        // 6. Simpan ke file JSON
        val gson = Gson()
        val jsonData = gson.toJson(allData)
        val jsonFile = File(backupDir, "data.json")
        jsonFile.writeText(jsonData)

        Toast.makeText(context, "Export berhasil ke ${jsonFile.absolutePath}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Export gagal: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
