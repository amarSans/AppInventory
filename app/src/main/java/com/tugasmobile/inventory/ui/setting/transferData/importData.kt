package com.tugasmobile.inventory.ui.setting.transferData

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_BARANG
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_BARANG_KELUAR
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_BARANG_MASUK
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_HISTORY
import com.tugasmobile.inventory.database.BrgDatabaseHelper.Companion.TABLE_STOK
import java.io.File

fun importData(context: Context, database: SQLiteDatabase) {
    val backupDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS,),
        "BackupInventaTa"
    )
    val imageBackupDir = File(backupDir, "InventoryApp")
    val jsonFile = File(backupDir, "data.json")

    if (!jsonFile.exists()) {
        Toast.makeText(context, "File backup tidak ditemukan", Toast.LENGTH_LONG).show()
        return
    }

    try {
        val gson = Gson()
        val jsonData = jsonFile.readText()
        val allData = gson.fromJson(jsonData, Map::class.java)

        database.beginTransaction()

        // Kosongkan dulu tabel
        database.execSQL("DELETE FROM $TABLE_BARANG")
        database.execSQL("DELETE FROM $TABLE_STOK")
        database.execSQL("DELETE FROM $TABLE_BARANG_MASUK")
        database.execSQL("DELETE FROM $TABLE_BARANG_KELUAR")
        database.execSQL("DELETE FROM $TABLE_HISTORY")

        val imageTargetDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "InventoryApp"
        )
        if (!imageTargetDir.exists()) imageTargetDir.mkdirs()

        // 1. Barang
        val barangJson = gson.toJson(allData["barang"])
        val barangListType = object : TypeToken<List<ItemBarang>>() {}.type
        val barangList: List<ItemBarang> = gson.fromJson(barangJson, barangListType)
        for (item in barangList) {
            val oldPath = item.gambar
            val fileName = File(oldPath).name
            val srcFile = File(imageBackupDir, fileName)
            val destFile = File(imageTargetDir, fileName)
            Log.d("IMPORT_GAMBAR", "oldPath: $oldPath")
            Log.d("IMPORT_GAMBAR", "fileName: $fileName")
            Log.d("IMPORT_GAMBAR", "srcFile path: ${srcFile.absolutePath}")
            Log.d("IMPORT_GAMBAR", "srcFile exists: ${srcFile.exists()}")
            // Salin gambar
            if (srcFile.exists()) {
                srcFile.copyTo(destFile, overwrite = true)
            }
            val newUri = addImageToMediaStore(context, destFile)
            val newImagePath = newUri.toString()

            // Simpan ke database
            val stmt = database.compileStatement("INSERT INTO $TABLE_BARANG VALUES (?, ?, ?, ?, ?)")
            stmt.bindString(1, item.id_barang)
            stmt.bindString(2, item.merek_barang)
            stmt.bindString(3, item.karakteristik)
            stmt.bindString(4, newImagePath)
            stmt.bindString(5,item.lastUpdate)
            stmt.executeInsert()

            srcFile.delete()
        }

        // 2. Stok
        val stokJson = gson.toJson(allData["stok"])
        val stokListType = object : TypeToken<List<Stok>>() {}.type
        val stokList: List<Stok> = gson.fromJson(stokJson, stokListType)

// Simpan ke database
        for (stok in stokList) {
            val stmt = database.compileStatement("INSERT INTO $TABLE_STOK VALUES (?, ?, ?, ?)")
            stmt.bindLong(1, stok.idStok)
            stmt.bindString(2, stok.id_barang)
            stmt.bindString(3, stok.ukuranwarna)
            stmt.bindLong(4, stok.stokBarang.toLong())
            stmt.executeInsert()
        }

        // 3. Barang Masuk
        val masukJson = gson.toJson(allData["barang_masuk"])
        val masukListType = object : TypeToken<List<BarangIn>>() {}.type
        val masukList: List<BarangIn> = gson.fromJson(masukJson, masukListType)

        for (brg in masukList) {
            val stmt = database.compileStatement("INSERT INTO $TABLE_BARANG_MASUK VALUES (?, ?, ?, ?, ?)")
            stmt.bindLong(1, brg.IdBrgMasuk)
            stmt.bindString(2, brg.id_barang)
            stmt.bindString(3, brg.Tgl_Masuk)
            stmt.bindLong(4, brg.Harga_Modal.toLong())
            stmt.bindString(5, brg.Nama_Toko)
            stmt.executeInsert()
        }

        // 4. Barang Keluar
        val keluarJson = gson.toJson(allData["barang_keluar"])
        val keluarListType = object : TypeToken<List<BarangOut>>() {}.type
        val keluarList: List<BarangOut> = gson.fromJson(keluarJson, keluarListType)

        for (brg in keluarList) {
            val stmt = database.compileStatement("INSERT INTO $TABLE_BARANG_KELUAR VALUES (?, ?, ?, ?, ?, ?)")
            stmt.bindLong(1, brg.IdBrgKeluar)
            stmt.bindString(2, brg.id_barang)
            stmt.bindString(3, brg.ukuran_warna)
            stmt.bindLong(4, brg.stok_keluar.toLong())
            stmt.bindString(5, brg.Tgl_Keluar)
            stmt.bindLong(6, brg.Hrg_Beli.toLong())
            stmt.executeInsert()
        }

        // 5. History
        val historyJson = gson.toJson(allData["story"])
        val historyListType = object : TypeToken<List<History>>() {}.type
        val historyList: List<History> = gson.fromJson(historyJson, historyListType)

        for (his in historyList) {
            val stmt = database.compileStatement("INSERT INTO $TABLE_HISTORY VALUES (?, ?, ?, ?, ?)")
            stmt.bindLong(1, his.id.toLong())
            stmt.bindString(2, his.waktu)
            stmt.bindString(3, his.kodeBarang)
            stmt.bindString(4, his.stok)
            stmt.bindString(5, his.jenisData)
            stmt.executeInsert()
        }

        database.setTransactionSuccessful()
        Toast.makeText(context, "Import berhasil!", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Import gagal: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    } finally {
        database.endTransaction()
    }
}
fun addImageToMediaStore(context: Context, imageFile: File): Uri? {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Untuk Android 10 ke atas, gunakan RELATIVE_PATH
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/InventoryApp")
        } else {
            // Untuk Android 9 ke bawah, gunakan _data (deprecated tapi masih jalan)
            put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
        }
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { output ->
            imageFile.inputStream().copyTo(output)
        }
    }

    return uri
}

