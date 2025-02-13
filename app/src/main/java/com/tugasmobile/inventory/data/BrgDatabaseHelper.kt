package com.tugasmobile.inventory.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BrgDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Inventaris.db"
        private const val DATABASE_VERSION = 2

        // Tabel Barang
        const val TABLE_BARANG = "barang"
        const val COLUMN_ID_BARANG = "id_barang"
        const val COLUMN_NAMA_BARANG = "nama_barang"
        const val COLUMN_KODE_BARANG = "kode_barang"
        const val COLUMN_KATEGORI = "kategori"
        const val COLUMN_GAMBAR = "gambar"

        // Tabel Stok
        const val TABLE_STOK = "stok"
        const val COLUMN_ID_STOK = "id_stok"
        const val COLUMN_WARNA = "warna"
        const val COLUMN_UKURAN = "ukuran"
        const val COLUMN_STOK = "stok"

        // Tabel Barang Masuk
        const val TABLE_BARANG_MASUK = "barang_masuk"
        const val COLUMN_ID_MASUK = "id_masuk"
        const val COLUMN_TANGGAL_MASUK = "tanggal_masuk"
        const val COLUMN_HARGA_JUAL = "harga_jual"
        const val COLUMN_NAMA_TOKO = "nama_toko"

        // Tabel Barang Keluar
        const val TABLE_BARANG_KELUAR = "barang_keluar"
        const val COLUMN_ID_KELUAR = "id_keluar"
        const val COLUMN_TANGGAL_KELUAR = "tanggal_keluar"
        const val COLUMN_HARGA_BELI = "harga_beli"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createBarangTable = """
            CREATE TABLE $TABLE_BARANG (
                $COLUMN_ID_BARANG INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAMA_BARANG TEXT,
                $COLUMN_KODE_BARANG TEXT UNIQUE,
                $COLUMN_KATEGORI TEXT,
                $COLUMN_GAMBAR TEXT
            )
        """.trimIndent()

        val createStokTable = """
            CREATE TABLE $TABLE_STOK (
                $COLUMN_ID_STOK INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID_BARANG INTEGER,
                $COLUMN_WARNA TEXT,
                $COLUMN_UKURAN TEXT,
                $COLUMN_STOK INTEGER,
                FOREIGN KEY ($COLUMN_ID_BARANG) REFERENCES $TABLE_BARANG ($COLUMN_ID_BARANG) ON DELETE CASCADE
            )
        """.trimIndent()

        val createBarangMasukTable = """
            CREATE TABLE $TABLE_BARANG_MASUK (
                $COLUMN_ID_MASUK INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID_BARANG INTEGER,
                $COLUMN_TANGGAL_MASUK TEXT,
                $COLUMN_HARGA_JUAL INTEGER,
                $COLUMN_NAMA_TOKO TEXT,
                FOREIGN KEY ($COLUMN_ID_BARANG) REFERENCES $TABLE_BARANG ($COLUMN_ID_BARANG) ON DELETE CASCADE
            )
        """.trimIndent()

        val createBarangKeluarTable = """
            CREATE TABLE $TABLE_BARANG_KELUAR (
                $COLUMN_ID_KELUAR INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID_BARANG INTEGER,
                $COLUMN_TANGGAL_KELUAR TEXT,
                $COLUMN_HARGA_BELI INTEGER,
                FOREIGN KEY ($COLUMN_ID_BARANG) REFERENCES $TABLE_BARANG ($COLUMN_ID_BARANG) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createBarangTable)
        db.execSQL(createStokTable)
        db.execSQL(createBarangMasukTable)
        db.execSQL(createBarangKeluarTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BARANG")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STOK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BARANG_MASUK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BARANG_KELUAR")
        onCreate(db)
    }
    fun insertBarang(barang:Barang):Long{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_BARANG, barang.namaBarang)
            put(COLUMN_KODE_BARANG, barang.kodeBarang)
            put(COLUMN_GAMBAR, barang.gambar)
        }
        val id = db.insert(TABLE_BARANG, null, values)
        db.close()
        return id
    }
    fun insertStok(stok: Stok):Long{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_BARANG, stok.id_barang)
            put(COLUMN_WARNA, stok.warna.joinToString (","))
            put(COLUMN_UKURAN, stok.ukuran)
            put(COLUMN_STOK, stok.stok)
        }
        val id = db.insert(TABLE_STOK, null, values)
        db.close()
        return id
    }
    fun insertBarangMasuk(masuk: BarangMasuk):Long{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_BARANG, masuk.id_barang)
            put(COLUMN_TANGGAL_MASUK, masuk.Tgl_Masuk)
            put(COLUMN_HARGA_JUAL, masuk.Harga_Modal)
            put(COLUMN_NAMA_TOKO, masuk.Nama_Toko)
        }
        val id = db.insert(TABLE_BARANG_MASUK, null, values)
        db.close()
        return id
    }
    fun insertBarangKeluar(keluar: BarangKeluar):Long{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_BARANG, keluar.id_barang)
            put(COLUMN_TANGGAL_KELUAR, keluar.Tgl_Keluar)
            put(COLUMN_HARGA_JUAL, keluar.Hrg_Beli)
        }
        val id = db.insert(TABLE_BARANG_KELUAR, null, values)
        db.close()
        return id
    }

}