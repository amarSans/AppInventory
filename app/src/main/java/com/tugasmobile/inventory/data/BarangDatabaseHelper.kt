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
        private const val DATABASE_VERSION = 1
        const val TABLE_LAPORAN = "laporan"
        const val COLUMN_ID = "id"
        const val COLUMN_NAMA_BARANG = "nama_barang"
        const val COLUMN_KODE_BARANG = "kode_barang"
        const val COLUMN_STOK = "stok"
        const val COLUMN_HARGA = "harga"
        const val COLUMN_WARNA = "warna"
        const val COLUMN_WAKTU = "waktu"
        const val `COLUMN_NAMA_TOKO` = "kategori"
        const val COLUMN_UKURAN = "ukuran"
        const val COLUMN_GAMBAR = "gambar"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_LAPORAN ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAMA_BARANG TEXT, "
                + "$COLUMN_KODE_BARANG TEXT, "
                + "$COLUMN_STOK INTEGER, "
                + "$COLUMN_HARGA INTEGER, "
                + "$COLUMN_WARNA TEXT, "
                + "$COLUMN_WAKTU TEXT, "
                + "$`COLUMN_NAMA_TOKO` TEXT, "
                + "$COLUMN_UKURAN TEXT, "
                + "$COLUMN_GAMBAR TEXT"
                +")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LAPORAN")
        onCreate(db)
    }

    // Metode untuk menambahkan barangPrototype
    fun insertLaporan(barangPrototype: BarangPrototype): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_BARANG, barangPrototype.namaBarang)
            put(COLUMN_KODE_BARANG, barangPrototype.kodeBarang)
            put(COLUMN_STOK, barangPrototype.stok)
            put(COLUMN_HARGA, barangPrototype.harga)
            put(COLUMN_WARNA, barangPrototype.warna.joinToString (","))
            put(COLUMN_WAKTU, barangPrototype.waktu)
            put(COLUMN_NAMA_TOKO, barangPrototype.nama_toko)
            put(COLUMN_UKURAN, barangPrototype.ukuran)
            put(COLUMN_GAMBAR, barangPrototype.gambar)
        }
        val id = db.insert(TABLE_LAPORAN, null, values)
        db.close()
        return id
    }

    fun getAllLaporan(): List<BarangPrototype> {
        val barangPrototypeList = mutableListOf<BarangPrototype>()
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
                    val waktu=it.getString(it.getColumnIndexOrThrow(COLUMN_WAKTU))
                    val kategori=it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA_TOKO))
                    val ukuran=it.getString(it.getColumnIndexOrThrow(COLUMN_UKURAN))
                    val gambarUri = Uri.parse(it.getString(it.getColumnIndexOrThrow(COLUMN_GAMBAR)))

                    // Membuat objek BarangPrototype
                    val barangPrototype = BarangPrototype(id, namaBarang, kodeBarang, stok, harga, warna, waktu, kategori, ukuran,
                        gambarUri.toString()
                    )
                    barangPrototypeList.add(barangPrototype)
                } while (it.moveToNext())
            }
        }
        cursor?.close()
        db.close()
        return barangPrototypeList
    }

    // Metode untuk menghapus barang berdasarkan ID
    fun deleteLaporan(id: Long): Int {
        val db = this.writableDatabase
        // Menghapus barang berdasarkan ID
        val result = db.delete(TABLE_LAPORAN, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun updateLaporan(barangPrototype: BarangPrototype): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_BARANG, barangPrototype.namaBarang)
            put(COLUMN_KODE_BARANG, barangPrototype.kodeBarang)
            put(COLUMN_STOK, barangPrototype.stok)
            put(COLUMN_HARGA, barangPrototype.harga)
            put(COLUMN_WARNA, barangPrototype.warna.joinToString (","))
            put(COLUMN_WAKTU, barangPrototype.waktu)
            put(COLUMN_NAMA_TOKO, barangPrototype.nama_toko)
            put(COLUMN_UKURAN, barangPrototype.ukuran)
            put(COLUMN_GAMBAR, barangPrototype.gambar.toString())
        }

        // Mengupdate barangPrototype berdasarkan ID
        val result = db.update(
            TABLE_LAPORAN,
            values,
            "$COLUMN_ID = ?",
            arrayOf(barangPrototype.id.toString())
        )

        db.close()
        return result
    }

    fun getLaporanById(id: Long): BarangPrototype? {
        val db = this.readableDatabase
        var barangPrototype: BarangPrototype? = null
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
                val waktuIndex = it.getColumnIndex(COLUMN_WAKTU)
                val namaTokoIndex = it.getColumnIndex(COLUMN_NAMA_TOKO)
                val ukuranIndex = it.getColumnIndex(COLUMN_UKURAN)
                val gambarIndex = it.getColumnIndex(COLUMN_GAMBAR)

                // Pastikan kolom tidak -1
                if (idIndex != -1 && namaBarangIndex != -1 && kodeBarangIndex != -1 && stokIndex != -1 &&
                    hargaIndex != -1 && warnaIndex != -1 && waktuIndex != -1&& namaTokoIndex != -1 &&
                    ukuranIndex != -1  && gambarIndex != -1) {

                    val idBarang = it.getLong(idIndex)
                    val namaBarang = it.getString(namaBarangIndex)
                    val kodeBarang = it.getString(kodeBarangIndex)
                    val stok = it.getInt(stokIndex)
                    val harga = it.getInt(hargaIndex)
                    val warna = it.getString(it.getColumnIndexOrThrow(COLUMN_WARNA)).split(",")
                    val waktu = it.getString(waktuIndex)
                    val namaToko = it.getString(namaTokoIndex)
                    val ukuran = it.getString(ukuranIndex)
                    val gambarUri = Uri.parse(it.getString(gambarIndex))

                    barangPrototype = BarangPrototype(idBarang, namaBarang, kodeBarang, stok, harga, warna,waktu, namaToko, ukuran ,gambarUri.toString()
                    )
                }
            }
        }
        cursor?.close()
        db.close()
        return barangPrototype
    }
    fun updateWarna(barangId: Long, newColors: List<String>): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WARNA, newColors.joinToString(","))
        }

        // Mengupdate warna berdasarkan ID
        val result = db.update(
            TABLE_LAPORAN,
            values,
            "$COLUMN_ID = ?",
            arrayOf(barangId.toString())
        )

        db.close()
        return result
    }
}