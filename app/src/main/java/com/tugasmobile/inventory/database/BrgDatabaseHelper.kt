package com.tugasmobile.inventory.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.data.DataBarangAkses
import com.tugasmobile.inventory.data.DataSearch
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.SettingData
import com.tugasmobile.inventory.data.Stok
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BrgDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Inventaris.db"
        private const val DATABASE_VERSION = 2

        @Volatile
        private var INSTANCE: BrgDatabaseHelper? = null

        fun getInstance(context: Context): BrgDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BrgDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }

        // Tabel Barang
        const val TABLE_BARANG = "barang"
        const val COLUMN_KODE_BARANG = "kode_barang"
        const val COLUMN_MEREK_BARANG = "merek_barang"
        const val COLUMN_GAMBAR = "gambar"

        // Tabel Stok
        const val TABLE_STOK = "stok"
        const val COLUMN_ID_STOK = "id_stok"
        const val COLUMN_UKURAN_WARNA = "ukuran_warna"
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
        const val COLUMN_UKURAN_WARNA_KELUAR = "ukuran_warna"
        const val COLUMN_STOK_KELUAR = "stok_keluar"
        const val COLUMN_HARGA_BELI = "harga_beli"

        //tabel setting
        const val TABLE_SETTINGS = "settings"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTIF_ENABLED = "notif_enabled"
        const val COLUMN_NOTIF_TIME = "notif_time"
        const val COLUMN_START_DAY = "start_day"
        const val COLUMN_END_DAY = "end_day"

        const val TABLE_HISTORY = "history"
        const val COLUMN_ID_HISTORY = "id"
        const val COLUMN_WAKTU_HISTORY = "waktu"
        const val COLUMN_KODE_BARANG_HISTORY = "kode_barang"
        const val COLUMN_STOK_HISTORY = "stok"
        const val COLUMN_UKURAN_WARNA_HISTORY = "ukuran_warna"
        const val COLUMN_HARGA_HISTORY = "harga"
        const val COLUMN_JENIS_DATA_HISTORY = "jenis_data"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createBarangTable = """
            CREATE TABLE $TABLE_BARANG (
                $COLUMN_KODE_BARANG TEXT PRIMARY KEY,
                $COLUMN_MEREK_BARANG TEXT NOT NULL,
                $COLUMN_GAMBAR TEXT DEFAULT NULL
            )
        """.trimIndent()

        val createStokTable = """
            CREATE TABLE $TABLE_STOK (
                $COLUMN_ID_STOK INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_KODE_BARANG TEXT,
                $COLUMN_UKURAN_WARNA TEXT DEFAULT '',
                $COLUMN_STOK INTEGER DEFAULT 0,
                FOREIGN KEY ($COLUMN_KODE_BARANG) REFERENCES $TABLE_BARANG ($COLUMN_KODE_BARANG) ON DELETE CASCADE
            )
        """.trimIndent()

        val createBarangMasukTable = """
            CREATE TABLE $TABLE_BARANG_MASUK (
                $COLUMN_ID_MASUK INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_KODE_BARANG TEXT,
                $COLUMN_TANGGAL_MASUK TEXT NOT NULL,
                $COLUMN_HARGA_JUAL INTEGER NOT NULL,
                $COLUMN_NAMA_TOKO TEXT DEFAULT NULL,
                FOREIGN KEY ($COLUMN_KODE_BARANG) REFERENCES $TABLE_BARANG ($COLUMN_KODE_BARANG) ON DELETE CASCADE
            )
        """.trimIndent()

        val createBarangKeluarTable = """
            CREATE TABLE $TABLE_BARANG_KELUAR (
                $COLUMN_ID_KELUAR INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_KODE_BARANG TEXT,
                $COLUMN_UKURAN_WARNA_KELUAR TEXT,
                $COLUMN_STOK_KELUAR INTEGER NOT NULL,
                $COLUMN_TANGGAL_KELUAR TEXT NOT NULL,
                $COLUMN_HARGA_BELI INTEGER,
                FOREIGN KEY ($COLUMN_KODE_BARANG) REFERENCES $TABLE_BARANG ($COLUMN_KODE_BARANG) ON DELETE CASCADE
            )
        """.trimIndent()

        val createSettingTable = """
            CREATE TABLE $TABLE_SETTINGS (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_NOTIF_ENABLED INTEGER,
                $COLUMN_NOTIF_TIME TEXT,
                $COLUMN_START_DAY TEXT,
                $COLUMN_END_DAY TEXT
            )
        """.trimIndent()
        val createHistoryTable = """
            CREATE TABLE $TABLE_HISTORY (
                $COLUMN_ID_HISTORY INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_WAKTU_HISTORY TEXT,
                $COLUMN_KODE_BARANG_HISTORY TEXT,
                $COLUMN_STOK_HISTORY TEXT,
                $COLUMN_UKURAN_WARNA_HISTORY TEXT,
                $COLUMN_HARGA_HISTORY TEXT,
                $COLUMN_JENIS_DATA_HISTORY INTEGER
            )
        """.trimIndent()


        db.execSQL(createBarangTable)
        db.execSQL(createStokTable)
        db.execSQL(createBarangMasukTable)
        db.execSQL(createBarangKeluarTable)
        db.execSQL(createSettingTable)
        db.execSQL(createHistoryTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BARANG")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STOK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BARANG_MASUK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BARANG_KELUAR")
        onCreate(db)
    }

    fun insertInputBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn) {
        val db = this.writableDatabase
        db.beginTransaction()
        return try {
            val valuesBarang = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_MEREK_BARANG, barang.merek_barang)
                put(COLUMN_GAMBAR, barang.gambar)
            }
            val barangId = db.insert(TABLE_BARANG, null, valuesBarang)
            if (barangId == -1L) throw Exception("terjadi kegagalan")

            val valuesStok = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_UKURAN_WARNA, stok.ukuranwarna.joinToString(","))
                put(COLUMN_STOK, stok.stokBarang)
            }
            val stokId = db.insert(TABLE_STOK, null, valuesStok)
            if (stokId == -1L) throw Exception("Gagal menyimpan stok")

            val valuesBarangMasuk = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_TANGGAL_MASUK, barangIn.Tgl_Masuk)
                put(COLUMN_HARGA_JUAL, barangIn.Harga_Modal)
                put(COLUMN_NAMA_TOKO, barangIn.Nama_Toko)
            }
            val barangMasukId = db.insert(TABLE_BARANG_MASUK, null, valuesBarangMasuk)
            if (barangMasukId == -1L) throw Exception("Gagal menyimpan barang masuk")
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }
    }

    fun insertBarangKeluar(barangOut: BarangOut): Long {
        val db = this.writableDatabase
        var id = -1L

        try {
            db.beginTransaction()

            // 1. Insert data ke tabel barang_keluar
            val values = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barangOut.id_barang)
                put(COLUMN_UKURAN_WARNA_KELUAR, barangOut.ukuran_warna)
                put(COLUMN_STOK_KELUAR, barangOut.stok_keluar)
                put(COLUMN_TANGGAL_KELUAR, barangOut.Tgl_Keluar)
                put(COLUMN_HARGA_BELI, barangOut.Hrg_Beli)
            }
            id = db.insert(TABLE_BARANG_KELUAR, null, values)

            if (id == -1L) {
                throw Exception("Gagal memasukkan data barang keluar")
            }

            // 2. Kurangi stok di tabel stok
            val stokCursor = db.rawQuery(
                "SELECT $COLUMN_STOK, $COLUMN_UKURAN_WARNA FROM $TABLE_STOK WHERE $COLUMN_KODE_BARANG = ?",
                arrayOf(barangOut.id_barang)
            )

            if (stokCursor.moveToFirst()) {
                val currentStok = stokCursor.getInt(stokCursor.getColumnIndexOrThrow(COLUMN_STOK))
                val currentUkuranWarna = stokCursor.getString(
                    stokCursor.getColumnIndexOrThrow(
                        COLUMN_UKURAN_WARNA
                    )
                )

                // Kurangi stok
                val newStok = currentStok - barangOut.stok_keluar
                if (newStok < 0) {
                    throw Exception("Stok tidak mencukupi")
                }

                // Update stok
                val updateValues = ContentValues().apply {
                    put(COLUMN_STOK, newStok)
                }
                db.update(
                    TABLE_STOK,
                    updateValues,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(barangOut.id_barang)
                )

                // 3. Hapus ukuran jika ada
                if (barangOut.ukuran_warna.isNotEmpty()) {
                    val currentUkuranList =
                        currentUkuranWarna.split(",").map { it.trim() }.toMutableList()
                    val ukuranToRemove = barangOut.ukuran_warna.split(",").map { it.trim() }

                    for (ukuran in ukuranToRemove) {
                        val index = currentUkuranList.indexOf(ukuran)
                        if (index != -1) {
                            currentUkuranList.removeAt(index) // Hapus hanya satu elemen yang cocok
                        }
                    }

                    val newUkuran = currentUkuranList.joinToString(",")
                    val updateUkuranValues = ContentValues().apply {
                        put(COLUMN_UKURAN_WARNA, newUkuran)
                    }
                    db.update(
                        TABLE_STOK,
                        updateUkuranValues,
                        "$COLUMN_KODE_BARANG = ?",
                        arrayOf(barangOut.id_barang)
                    )
                    if (newStok == 0) {
                        val resetValues = ContentValues().apply {
                            put(COLUMN_UKURAN_WARNA, "0 kosong") // Set "0 kosong" jika stok habis
                        }
                        db.update(
                            TABLE_STOK,
                            resetValues,
                            "$COLUMN_KODE_BARANG = ?",
                            arrayOf(barangOut.id_barang)
                        )
                    }
                }
            } else {
                throw Exception("Data stok tidak ditemukan")
            }

            stokCursor.close()
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("BarangKeluar", "Error: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return id
    }

    fun searchBarang(query: String): List<DataSearch> {
        val resultList = mutableListOf<DataSearch>()
        val db = this.readableDatabase

        val selectQuery = """
        SELECT $TABLE_BARANG.$COLUMN_KODE_BARANG, 
               $TABLE_BARANG.$COLUMN_MEREK_BARANG,
               $TABLE_BARANG_MASUK.$COLUMN_NAMA_TOKO
        FROM $TABLE_BARANG
        LEFT JOIN $TABLE_BARANG_MASUK 
        ON $TABLE_BARANG.$COLUMN_KODE_BARANG = $TABLE_BARANG_MASUK.$COLUMN_KODE_BARANG
        WHERE $TABLE_BARANG.$COLUMN_MEREK_BARANG LIKE ? 
           OR $TABLE_BARANG.$COLUMN_KODE_BARANG LIKE ? 
           OR $TABLE_BARANG_MASUK.$COLUMN_NAMA_TOKO LIKE ?
        GROUP BY $TABLE_BARANG.$COLUMN_KODE_BARANG
    """.trimIndent()

        val cursor = db.rawQuery(selectQuery, arrayOf("%$query%", "%$query%", "%$query%"))

        if (cursor.moveToFirst()) {
            do {
                val idBarang = cursor.getString(0) ?: ""
                val namaBarang = cursor.getString(1) ?: ""
                val namaToko = cursor.getString(2) ?: ""

                // Format hasil: "Kode Barang - Nama Barang (Nama Toko)"
                val dataSearch = DataSearch(idBarang, namaBarang, namaToko)
                resultList.add(dataSearch)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return resultList
    }

    fun getAllBarang(): List<DataBarangAkses> {
        val barangList = mutableListOf<DataBarangAkses>()
        val db = this.readableDatabase
        val query = """
            SELECT b.$COLUMN_KODE_BARANG, b.$COLUMN_MEREK_BARANG, 
                   b.$COLUMN_GAMBAR, s.$COLUMN_STOK, s.$COLUMN_UKURAN_WARNA,
                   m.$COLUMN_TANGGAL_MASUK, m.$COLUMN_HARGA_JUAL, m.$COLUMN_NAMA_TOKO
            FROM $TABLE_BARANG b
            LEFT JOIN $TABLE_STOK s ON b.$COLUMN_KODE_BARANG = s.$COLUMN_KODE_BARANG
            LEFT JOIN $TABLE_BARANG_MASUK m ON b.$COLUMN_KODE_BARANG = m.$COLUMN_KODE_BARANG
        """.trimIndent()
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val idBarang =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_BARANG)) ?: ""
                val namaBarang =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEREK_BARANG)) ?: ""
                val gambar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR)) ?: ""
                val stok = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOK))
                val ukuranwarna =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UKURAN_WARNA))?.split(",")
                        ?: emptyList()
                val waktu =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL_MASUK)) ?: ""
                val harga = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HARGA_JUAL))
                val namaToko =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_TOKO)) ?: ""

                val barang = DataBarangAkses(
                    idBarang,
                    namaBarang,
                    stok,
                    harga,
                    ukuranwarna,
                    waktu,
                    namaToko,
                    gambar
                )
                barangList.add(barang)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return barangList
    }

    fun cekKodeBarangAda(kode: String): Boolean {
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT COUNT(*) FROM $TABLE_BARANG WHERE kode_barang = ?", arrayOf(kode))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    fun updateWarna(barangId: String, newColors: List<String>): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_UKURAN_WARNA, newColors.joinToString(","))
        }

        // Mengupdate warna berdasarkan ID
        val result = db.update(
            TABLE_STOK,
            values,
            "$COLUMN_KODE_BARANG = ?",
            arrayOf(barangId)
        )

        db.close()
        return result
    }

    fun deleteBarang(id: String): Int {
        val db = this.writableDatabase
        // Menghapus barang berdasarkan ID
        val result = db.delete(TABLE_BARANG, "$COLUMN_KODE_BARANG = ?", arrayOf(id))

        return result
    }

    fun updateBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Update TABLE_BARANG (hanya nama, kode, dan gambar)
            val valuesBarang = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_MEREK_BARANG, barang.merek_barang)
                put(COLUMN_GAMBAR, barang.gambar)
            }
            val resultBarang = db.update(
                TABLE_BARANG,
                valuesBarang,
                "$COLUMN_KODE_BARANG = ?",
                arrayOf(barang.id_barang)
            )

            val valuesStok = ContentValues().apply {
                put(COLUMN_STOK, stok.stokBarang)
                put(COLUMN_UKURAN_WARNA, stok.ukuranwarna.joinToString(","))
            }
            val resultStok = db.update(
                TABLE_STOK,
                valuesStok,
                "$COLUMN_KODE_BARANG = ?",
                arrayOf(stok.id_barang)
            )
            val valuesBarangMasuk = ContentValues().apply {
                put(COLUMN_HARGA_JUAL, barangIn.Harga_Modal)
                put(COLUMN_TANGGAL_MASUK, barangIn.Tgl_Masuk)
                put(COLUMN_NAMA_TOKO, barangIn.Nama_Toko)
            }
            val resultBarangMasuk = db.update(
                TABLE_BARANG_MASUK,
                valuesBarangMasuk,
                "$COLUMN_KODE_BARANG = ?",
                arrayOf(barangIn.id_barang)
            )

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

    fun getBarangById(id: String): Triple<ItemBarang?, Stok?, BarangIn?> {
        val db = this.readableDatabase
        val query = """
            SELECT b.$COLUMN_KODE_BARANG, b.$COLUMN_MEREK_BARANG,
               b.$COLUMN_GAMBAR, s.$COLUMN_ID_STOK, s.$COLUMN_STOK, s.$COLUMN_UKURAN_WARNA,
               m.$COLUMN_ID_MASUK, m.$COLUMN_TANGGAL_MASUK, m.$COLUMN_HARGA_JUAL, m.$COLUMN_NAMA_TOKO
        FROM $TABLE_BARANG b
        LEFT JOIN $TABLE_STOK s ON b.$COLUMN_KODE_BARANG = s.$COLUMN_KODE_BARANG
        LEFT JOIN $TABLE_BARANG_MASUK m ON b.$COLUMN_KODE_BARANG = m.$COLUMN_KODE_BARANG
        WHERE b.$COLUMN_KODE_BARANG = ?
        """.trimIndent()
        var itemBarang: ItemBarang? = null
        var stok: Stok? = null
        var barangIn: BarangIn? = null
        val cursor: Cursor? = db.rawQuery(query, arrayOf(id))

        // Memeriksa apakah cursor tidak null dan memiliki data
        cursor?.use {
            if (it.moveToFirst()) {
                val idBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_KODE_BARANG)) ?: ""
                val namaBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_MEREK_BARANG)) ?: ""
                val gambarUri = Uri.parse(it.getString(it.getColumnIndexOrThrow(COLUMN_GAMBAR)))
                val idStok = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_STOK))
                val stokJumlah = it.getInt(it.getColumnIndexOrThrow(COLUMN_STOK)) ?: 0
                val ukuranwarna =
                    it.getString(it.getColumnIndexOrThrow(COLUMN_UKURAN_WARNA))?.split(",")
                        ?: emptyList()
                val idMasuk = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_MASUK))
                val tanggalMasuk =
                    it.getString(it.getColumnIndexOrThrow(COLUMN_TANGGAL_MASUK)) ?: ""
                val hargaJual = it.getInt(it.getColumnIndexOrThrow(COLUMN_HARGA_JUAL)) ?: 0
                val namaToko = it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA_TOKO)) ?: ""

                // Inisialisasi objek
                itemBarang = ItemBarang(idBarang, namaBarang, gambarUri.toString())
                stok = Stok(idStok, idBarang, stokJumlah, ukuranwarna)
                barangIn = BarangIn(idMasuk, idBarang, tanggalMasuk, hargaJual, namaToko)
            }
        }
        return Triple(itemBarang, stok, barangIn)
    }

    fun saveSetting(setting: SettingData) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, 1) // Hanya satu baris data
            put(COLUMN_NOTIF_ENABLED, if (setting.isNotifEnabled) 1 else 0)
            put(COLUMN_NOTIF_TIME, setting.notifTime)
            put(COLUMN_START_DAY, setting.startDay)
            put(COLUMN_END_DAY, setting.endDay)
        }
        db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun getSetting(): SettingData? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SETTINGS,
            null,
            "$COLUMN_ID = ?",
            arrayOf("1"),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val isNotifEnabled =
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIF_ENABLED)) == 1
            val notifTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTIF_TIME))
            val startDay = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DAY))
            val endDay = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DAY))
            SettingData(isNotifEnabled, notifTime, startDay, endDay)
        } else {
            null
        }
    }

    fun insertHistory(item: History) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_WAKTU_HISTORY, item.waktu)
                put(COLUMN_KODE_BARANG_HISTORY, item.kodeBarang)
                put(COLUMN_STOK_HISTORY, item.stok)
                put(COLUMN_UKURAN_WARNA_HISTORY, item.ukuranWarna)
                put(COLUMN_HARGA_HISTORY, item.harga)
                put(COLUMN_JENIS_DATA_HISTORY, if (item.jenisData) 1 else 0)
            }
            db.insert(TABLE_HISTORY, null, values)
            db.close()
        }
    }

    fun getAllHistoryItems(): List<History> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_HISTORY}", null)
        val items = mutableListOf<History>()
        if (cursor.moveToFirst()) {
            do {

                val idhistory =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_HISTORY))
                val historwaktu =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAKTU_HISTORY))
                val historykodeBarang =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_BARANG_HISTORY))
                val historystok =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOK_HISTORY))
                val historyukuranWarna = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_UKURAN_WARNA_HISTORY)
                )
                val historyharga =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARGA_HISTORY))
                val historyjenisData =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_JENIS_DATA_HISTORY)) == 1

                val history = History(
                    idhistory,
                    historwaktu,
                    historykodeBarang,
                    historystok,
                    historyukuranWarna,
                    historyharga,
                    historyjenisData
                )
                items.add(history)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }


}