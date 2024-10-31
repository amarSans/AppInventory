package com.tugasmobile.inventory.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class BarangDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "inventory.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_LAPORAN = "laporan"
        const val COLUMN_ID = "id"
        const val COLUMN_NAMA_BARANG = "nama_barang"
        const val COLUMN_KODE_BARANG = "kode_barang"
        const val COLUMN_STOK = "stok"
        const val COLUMN_HARGA = "harga"
        const val COLUMN_WARNA = "warna"/*
        const val COLUMN_KATEGORI = "kategori"
        const val COLUMN_WAKTU = "waktu"
        const val COLUMN_UKURAN = "ukuran"
        const val COLUMN_GAMBAR = "gambar"*/
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_LAPORAN ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAMA_BARANG TEXT, "
                + "$COLUMN_KODE_BARANG TEXT, "
                + "$COLUMN_STOK INTEGER, "
                + "$COLUMN_HARGA INTEGER, "
                + "$COLUMN_WARNA TEXT "
                /* + "$COLUMN_KATEGORI TEXT, "
                 + "$COLUMN_UKURAN TEXT, "
                 + "$COLUMN_WAKTU TEXT, "
                 + "$COLUMN_GAMBAR TEXT)"*/
                +")") // Menyimpan gambar sebagai URI dalam bentuk string
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LAPORAN")
        onCreate(db)
    }

    // Metode untuk menambahkan barang
    fun insertLaporan(barang: Barang): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_BARANG, barang.namaBarang)
            put(COLUMN_KODE_BARANG, barang.kodeBarang)
            put(COLUMN_STOK, barang.stok)
            put(COLUMN_HARGA, barang.harga)
             put(COLUMN_WARNA, barang.warna.joinToString (","))
            /* put(COLUMN_KATEGORI, barang.kategori)
             put(COLUMN_WAKTU, barang.waktu).toString()
             put(COLUMN_UKURAN, barang.ukuran)
             put(COLUMN_GAMBAR, barang.gambar.toString() )*/ // Simpan Uri gambar sebagai string
        }
        val id = db.insert(TABLE_LAPORAN, null, values)
        db.close()
        return id
    }

    fun getAllLaporan(): List<Barang> {
        val barangList = mutableListOf<Barang>()
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
                    val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                    val namaBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA_BARANG))
                    val kodeBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_KODE_BARANG))
                    val stok = it.getInt(it.getColumnIndexOrThrow(COLUMN_STOK))
                    val harga = it.getInt(it.getColumnIndexOrThrow(COLUMN_HARGA))
                    val warna = it.getString(it.getColumnIndexOrThrow(COLUMN_WARNA)).split(",")

                    barangList.add(Barang(id, namaBarang, kodeBarang, stok, harga, warna))
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        db.close()
        return barangList
    }

    // Metode untuk menghapus barang berdasarkan ID
    fun deleteLaporan(id: Long): Int {
        val db = this.writableDatabase
        // Menghapus barang berdasarkan ID
        val result = db.delete(TABLE_LAPORAN, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun updateLaporan(barang: Barang): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_BARANG, barang.namaBarang)
            put(COLUMN_KODE_BARANG, barang.kodeBarang)
            put(COLUMN_STOK, barang.stok)
            put(COLUMN_HARGA, barang.harga)
            put(COLUMN_WARNA, barang.warna.joinToString (","))
            /* put(COLUMN_KATEGORI, barang.kategori)
             put(COLUMN_UKURAN, barang.ukuran)
             put(COLUMN_WAKTU, barang.waktu)*/
           // put(COLUMN_GAMBAR, barang.gambar.toString()) // Simpan Uri gambar sebagai string
        }

        // Mengupdate barang berdasarkan ID
        val result = db.update(
            TABLE_LAPORAN,
            values,
            "$COLUMN_ID = ?",
            arrayOf(barang.id.toString())
        )

        db.close()
        return result
    }

    fun getLaporanById(id: Long): Barang? {
        val db = this.readableDatabase
        var barang: Barang? = null
        val cursor: Cursor? = db.query(
            TABLE_LAPORAN,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        // Memeriksa apakah cursor tidak null dan memiliki data
        cursor?.let {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val namaBarangIndex = it.getColumnIndex(COLUMN_NAMA_BARANG)
                val kodeBarangIndex = it.getColumnIndex(COLUMN_KODE_BARANG)
                val stokIndex = it.getColumnIndex(COLUMN_STOK)
                val hargaIndex = it.getColumnIndex(COLUMN_HARGA)
                val warnaIndex = it.getColumnIndex(COLUMN_WARNA)
                /*val kategoriIndex = it.getColumnIndex(COLUMN_KATEGORI)
                val ukuranIndex = it.getColumnIndex(COLUMN_UKURAN)
                val waktuIndex = it.getColumnIndex(COLUMN_WAKTU)*/
                /*val gambarIndex = it.getColumnIndex(COLUMN_GAMBAR)*/

                // Pastikan kolom tidak -1
                if (idIndex != -1 && namaBarangIndex != -1 && kodeBarangIndex != -1 && stokIndex != -1 &&
                    hargaIndex != -1 && warnaIndex != -1 /*&& kategoriIndex != -1 && ukuranIndex != -1 &&
                    waktuIndex != -1 && gambarIndex != -1*/) {

                    val idBarang = it.getLong(idIndex)
                    val namaBarang = it.getString(namaBarangIndex)
                    val kodeBarang = it.getString(kodeBarangIndex)
                    val stok = it.getInt(stokIndex)
                    val harga = it.getInt(hargaIndex)
                    val warna = it.getString(it.getColumnIndexOrThrow(COLUMN_WARNA)).split(",")
                    /*val kategori = it.getString(kategoriIndex)
                    val waktu = it.getString(waktuIndex)
                    val ukuran = it.getString(ukuranIndex)*/
                    //val gambarUri = Uri.parse(it.getString(gambarIndex)) // Convert string ke Uri

                    barang = Barang(idBarang, namaBarang, kodeBarang, stok, harga, warna/*,waktu, kategori, ukuran,*/ //,gambarUri
                    )
                }
            }
        }
        cursor?.close()
        db.close()
        return barang
    }
}