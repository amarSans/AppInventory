package com.tugasmobile.inventory.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.tugasmobile.inventory.data.BarangDatabaseHelper.Companion.COLUMN_ID

class BrgDatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Inventaris.db"
        private const val DATABASE_VERSION = 2

        // Tabel Barang
        const val TABLE_BARANG = "barang"
        const val COLUMN_ID_BARANG = "id_barang"
        const val COLUMN_NAMA_BARANG = "nama_barang"
        const val COLUMN_KODE_BARANG = "kode_barang"
        //const val COLUMN_TIPE_BARANG = "tipe_barang"
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

    fun insertInputBarang(barang:Barang1,stok:Stok,barangIn: BarangIn){
        val db = this.writableDatabase
        db.beginTransaction()
        return try {
            val valuesBarang = ContentValues().apply {
                put(COLUMN_NAMA_BARANG, barang.nama_barang)
                put(COLUMN_KODE_BARANG, barang.kode_barang)
                put(COLUMN_GAMBAR, barang.gambar)
            }
            val barangId = db.insert(TABLE_BARANG, null, valuesBarang)
            if (barangId == -1L) throw Exception("terjadi kegagalan")
            val valuesStok = ContentValues().apply {
                put(COLUMN_ID_BARANG, barangId)
                put(COLUMN_WARNA, stok.warna.joinToString(","))
                put(COLUMN_UKURAN, stok.ukuran)
                put(COLUMN_STOK, stok.stokBarang)
            }
            val stokId = db.insert(TABLE_STOK, null, valuesStok)
            if (stokId == -1L) throw Exception("Gagal menyimpan stok")
            val valuesBarangMasuk = ContentValues().apply {
                put(COLUMN_ID_BARANG, barangId)
                put(COLUMN_TANGGAL_MASUK, barangIn.Tgl_Masuk)
                put(COLUMN_HARGA_JUAL, barangIn.Harga_Modal)
                put(COLUMN_NAMA_TOKO, barangIn.Nama_Toko)
            }
            val barangMasukId = db.insert(TABLE_BARANG_MASUK, null, valuesBarangMasuk)
            if (barangMasukId == -1L) throw Exception("Gagal menyimpan barang masuk")
            db.setTransactionSuccessful()
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.endTransaction()
            db.close()
        }
    }
    fun insertBarangKeluar(barangOut: BarangOut): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID_BARANG, barangOut.id_barang)
            put(COLUMN_TANGGAL_KELUAR, barangOut.Tgl_Keluar)
            put(COLUMN_HARGA_BELI, barangOut.Hrg_Beli)
        }
        val id = db.insert(TABLE_BARANG_KELUAR, null, values)
        db.close()
        return id
    }

    fun getAllBarang(): List<DataBarangMasuk> {
        val barangList = mutableListOf<DataBarangMasuk>()
        val db = this.readableDatabase
        val query = """
            SELECT b.$COLUMN_ID_BARANG, b.$COLUMN_NAMA_BARANG, b.$COLUMN_KODE_BARANG, 
                   b.$COLUMN_GAMBAR, s.$COLUMN_STOK, s.$COLUMN_WARNA, s.$COLUMN_UKURAN, 
                   m.$COLUMN_TANGGAL_MASUK, m.$COLUMN_HARGA_JUAL, m.$COLUMN_NAMA_TOKO
            FROM $TABLE_BARANG b
            LEFT JOIN $TABLE_STOK s ON b.$COLUMN_ID_BARANG = s.$COLUMN_ID_BARANG
            LEFT JOIN $TABLE_BARANG_MASUK m ON b.$COLUMN_ID_BARANG = m.$COLUMN_ID_BARANG
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idBarang = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID_BARANG))
                val namaBarang = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_BARANG))
                val kodeBarang = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_BARANG))
                val gambar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR)) ?: ""
                val stok = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOK))
                val warna = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WARNA)).split(",")
                val ukuran = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UKURAN))
                val waktu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL_MASUK))
                val harga = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HARGA_JUAL))
                val namaToko = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_TOKO))

                val barang = DataBarangMasuk(idBarang, namaBarang, kodeBarang, stok, harga, warna, waktu, namaToko, ukuran, gambar)
                barangList.add(barang)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return barangList
    }
    fun updateWarna(barangId: Long, newColors: List<String>): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WARNA, newColors.joinToString(","))
        }

        // Mengupdate warna berdasarkan ID
        val result = db.update(
            TABLE_STOK,
            values,
            "$COLUMN_ID_BARANG = ?",
            arrayOf(barangId.toString())
        )

        db.close()
        return result
    }
    fun deleteBarang(id: Long): Int {
        val db = this.writableDatabase
        // Menghapus barang berdasarkan ID
        val result = db.delete(TABLE_BARANG, "$COLUMN_ID = ?", arrayOf(id.toString()))

        db.close()
        return result
    }
    fun updateBarang(barang: Barang1, stok: Stok, barangIn: BarangIn): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Update TABLE_BARANG (hanya nama, kode, dan gambar)
            val valuesBarang = ContentValues().apply {
                put(COLUMN_NAMA_BARANG, barang.nama_barang)
                put(COLUMN_KODE_BARANG, barang.kode_barang)
                put(COLUMN_GAMBAR, barang.gambar)
            }
            val resultBarang = db.update(
                TABLE_BARANG,
                valuesBarang,
                "$COLUMN_ID_BARANG = ?",
                arrayOf(barang.id_barang.toString())
            )

            // Update TABLE_STOK (stok, warna, ukuran)
            val valuesStok = ContentValues().apply {
                put(COLUMN_STOK, stok.stokBarang)
                put(COLUMN_WARNA, stok.warna.joinToString ( "," ))
                put(COLUMN_UKURAN, stok.ukuran)
            }
            val resultStok = db.update(
                TABLE_STOK,
                valuesStok,
                "$COLUMN_ID_BARANG = ?",
                arrayOf(stok.idStok.toString())
            )

            // Update TABLE_BARANG_MASUK (harga, tanggal masuk, nama toko)
            val valuesBarangMasuk = ContentValues().apply {
                put(COLUMN_HARGA_JUAL, barangIn.Harga_Modal)
                put(COLUMN_TANGGAL_MASUK, barangIn.Tgl_Masuk)
                put(COLUMN_NAMA_TOKO, barangIn.Nama_Toko)
            }
            val resultBarangMasuk = db.update(
                TABLE_BARANG_MASUK,
                valuesBarangMasuk,
                "$COLUMN_ID_BARANG = ?",
                arrayOf(barangIn.IdBrgMasuk.toString())
            )

            // Cek apakah semua update berhasil
            if (resultBarang > 0 && resultStok > 0 && resultBarangMasuk > 0) {
                db.setTransactionSuccessful()
                return true
            }
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }
    fun getBarangById(id: Long): Triple<Barang1?,Stok?, BarangIn?> {
        val db = this.readableDatabase
        val query = """
            SELECT b.$COLUMN_ID_BARANG, b.$COLUMN_NAMA_BARANG, b.$COLUMN_KODE_BARANG, 
               b.$COLUMN_GAMBAR, s.$COLUMN_ID_STOK, s.$COLUMN_STOK, s.$COLUMN_WARNA, s.$COLUMN_UKURAN, 
               m.$COLUMN_ID_MASUK, m.$COLUMN_TANGGAL_MASUK, m.$COLUMN_HARGA_JUAL, m.$COLUMN_NAMA_TOKO
        FROM $TABLE_BARANG b
        LEFT JOIN $TABLE_STOK s ON b.$COLUMN_ID_BARANG = s.$COLUMN_ID_BARANG
        LEFT JOIN $TABLE_BARANG_MASUK m ON b.$COLUMN_ID_BARANG = m.$COLUMN_ID_BARANG
        WHERE b.$COLUMN_ID_BARANG = ?
        """.trimIndent()
        var barang1:Barang1?=null
        var stok:Stok?=null
        var barangIn:BarangIn?=null
        val cursor: Cursor? = db.rawQuery(query, arrayOf(id.toString()))

        // Memeriksa apakah cursor tidak null dan memiliki data
        cursor?.use {
            if (it.moveToFirst()) {
                val idBarang = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_BARANG))
                val namaBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA_BARANG))
                val kodeBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_KODE_BARANG))
                val gambarUri = Uri.parse(it.getString(it.getColumnIndexOrThrow(COLUMN_GAMBAR)))

                val idStok=it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_STOK))
                val stokJumlah = it.getInt(it.getColumnIndexOrThrow(COLUMN_STOK))
                val warna = it.getString(it.getColumnIndexOrThrow(COLUMN_WARNA)).split(",")
                val ukuran = it.getString(it.getColumnIndexOrThrow(COLUMN_UKURAN))

                val idMasuk=it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_MASUK))
                val tanggalMasuk = it.getString(it.getColumnIndexOrThrow(COLUMN_TANGGAL_MASUK))
                val hargaJual = it.getInt(it.getColumnIndexOrThrow(COLUMN_HARGA_JUAL))
                val namaToko = it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA_TOKO))

                // Inisialisasi objek
                barang1 = Barang1(idBarang, namaBarang, kodeBarang, gambarUri.toString())
                stok = Stok(idStok,idBarang, stokJumlah,warna, ukuran )
                barangIn = BarangIn(idMasuk,idBarang, tanggalMasuk, hargaJual, namaToko)
            }
        }

        db.close()
        return Triple(barang1, stok, barangIn)
    }
}