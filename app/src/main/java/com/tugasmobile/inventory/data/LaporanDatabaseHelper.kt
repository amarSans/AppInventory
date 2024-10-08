package com.tugasmobile.inventory.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LaporanDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_LAPORAN = "laporan"
        const val COLUMN_ID = "id"
        const val COLUMN_NAMA_PRODUK = "nama_produk"
        const val COLUMN_STOK = "stok"
        const val COLUMN_HARGA = "harga"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_LAPORAN ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAMA_PRODUK TEXT, "
                + "$COLUMN_STOK INTEGER, "
                + "$COLUMN_HARGA REAL)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LAPORAN")
        onCreate(db)
    }

    // Metode untuk menambahkan laporan
    fun insertLaporan(laporan: Laporan): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_PRODUK, laporan.namaProduk)
            put(COLUMN_STOK, laporan.stok)
            put(COLUMN_HARGA, laporan.harga)
        }
        val id = db.insert(TABLE_LAPORAN, null, values)
        db.close()
        return id
    }

    // Metode untuk mendapatkan semua laporan
    fun getAllLaporan(): List<Laporan> {
        val laporanList = mutableListOf<Laporan>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_LAPORAN,
            null,
            null,
            null,
            null,
            null,
            null
        )

        // Memeriksa apakah cursor tidak null dan memiliki data
        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val idIndex = it.getColumnIndex(COLUMN_ID)
                    val namaProdukIndex = it.getColumnIndex(COLUMN_NAMA_PRODUK)
                    val stokIndex = it.getColumnIndex(COLUMN_STOK)
                    val hargaIndex = it.getColumnIndex(COLUMN_HARGA)

                    // Pastikan kolom tidak -1
                    if (idIndex != -1 && namaProdukIndex != -1 && stokIndex != -1 && hargaIndex != -1) {
                        val id = it.getLong(idIndex)
                        val namaProduk = it.getString(namaProdukIndex)
                        val stok = it.getInt(stokIndex)
                        val harga = it.getDouble(hargaIndex)
                        laporanList.add(Laporan(id, namaProduk, stok, harga))
                    }
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        db.close()
        return laporanList
    }
}