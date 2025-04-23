package com.muammar.inventory.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import com.muammar.inventory.data.BarangIn
import com.muammar.inventory.data.BarangMonitor
import com.muammar.inventory.data.BarangOut
import com.muammar.inventory.data.DataBarangAkses
import com.muammar.inventory.data.DataBarangHampirHabisHome
import com.muammar.inventory.data.DataSearch
import com.muammar.inventory.data.History
import com.muammar.inventory.data.ItemBarang
import com.muammar.inventory.data.SearchData
import com.muammar.inventory.data.SettingData
import com.muammar.inventory.data.Stok
import com.muammar.inventory.data.StokUpdate
import com.muammar.inventory.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BrgDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Inventaris.db"
        private const val DATABASE_VERSION = 3

        @Volatile
        private var INSTANCE: BrgDatabaseHelper? = null

        fun getInstance(context: Context): BrgDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BrgDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }


        const val TABLE_BARANG = "barang"
        const val COLUMN_KODE_BARANG = "kode_barang"
        const val COLUMN_MEREK_BARANG = "merek_barang"
        const val COLUMN_KARAKTERISTIK = "karakteristik"
        const val COLUMN_GAMBAR = "gambar"
        const val COLUMN_LAST_UPDATE = "last_update"


        const val TABLE_STOK = "table_stok"
        const val COLUMN_ID_STOK = "id_stok"
        const val COLUMN_UKURAN_WARNA = "ukuran_warna"
        const val COLUMN_STOK = "stok"


        const val TABLE_BARANG_MASUK = "barang_masuk"
        const val COLUMN_ID_MASUK = "id_masuk"
        const val COLUMN_TANGGAL_MASUK = "tanggal_masuk"
        const val COLUMN_HARGA_JUAL = "harga_jual"
        const val COLUMN_NAMA_TOKO = "nama_toko"


        const val TABLE_BARANG_KELUAR = "barang_keluar"
        const val COLUMN_ID_KELUAR = "id_keluar"
        const val COLUMN_TANGGAL_KELUAR = "tanggal_keluar"
        const val COLUMN_UKURAN_WARNA_KELUAR = "ukuran_warna"
        const val COLUMN_STOK_KELUAR = "stok_keluar"
        const val COLUMN_HARGA_BELI = "harga_beli"


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
        const val COLUMN_JENIS_DATA_HISTORY = "jenis_data"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createBarangTable = """
            CREATE TABLE $TABLE_BARANG (
                $COLUMN_KODE_BARANG TEXT PRIMARY KEY,
                $COLUMN_MEREK_BARANG TEXT NOT NULL,
                $COLUMN_KARAKTERISTIK TEXT DEFAULT NULL,
                $COLUMN_GAMBAR TEXT DEFAULT NULL,
                $COLUMN_LAST_UPDATE TEXT DEFAULT NULL
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
                $COLUMN_JENIS_DATA_HISTORY TEXT
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

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        val cursor = db.rawQuery("PRAGMA foreign_keys;", null)
        if (cursor.moveToFirst()) {
            val isEnabled = cursor.getInt(0) == 1
            Log.d("DB_FOREIGN_KEY", "Foreign keys enabled: $isEnabled")
        }
        cursor.close()
    }

    fun insertInputBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn) {
        val db = this.writableDatabase
        db.beginTransaction()
        return try {
            val valuesBarang = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_MEREK_BARANG, barang.merek_barang)
                put(COLUMN_KARAKTERISTIK, barang.karakteristik)
                put(COLUMN_GAMBAR, barang.gambar)
            }
            val barangId = db.insert(TABLE_BARANG, null, valuesBarang)
            if (barangId == -1L) throw Exception("terjadi kegagalan")

            val valuesStok = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_UKURAN_WARNA, stok.ukuranwarna)
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
            val updateLastUpdate =ContentValues().apply {
                put(COLUMN_LAST_UPDATE,barangIn.Tgl_Masuk)
            }
            db.update(TABLE_BARANG, updateLastUpdate, "$COLUMN_KODE_BARANG = ?", arrayOf(barang.id_barang))

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


                val newStok = currentStok - barangOut.stok_keluar
                if (newStok < 0) {
                    throw Exception("Stok tidak mencukupi")
                }


                val updateValues = ContentValues().apply {
                    put(COLUMN_STOK, newStok)
                }
                db.update(
                    TABLE_STOK,
                    updateValues,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(barangOut.id_barang)
                )


                if (barangOut.ukuran_warna.isNotEmpty()) {
                    val currentUkuranList =
                        currentUkuranWarna.split(",").map { it.trim() }.toMutableList()
                    val ukuranToRemove = barangOut.ukuran_warna.split(",").map { it.trim() }

                    for (ukuran in ukuranToRemove) {
                        val index = currentUkuranList.indexOf(ukuran)
                        if (index != -1) {
                            currentUkuranList.removeAt(index)
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
                            put(COLUMN_UKURAN_WARNA, "0 kosong")
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
            val updateLastUpdate = ContentValues().apply {
                put(COLUMN_LAST_UPDATE, barangOut.Tgl_Keluar)
            }
            db.update(
                TABLE_BARANG,
                updateLastUpdate,
                "$COLUMN_KODE_BARANG = ?",
                arrayOf(barangOut.id_barang)
            )
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("BarangKeluar", "Error: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return id
    }

    fun isBarangExist(kodeBarang: String): Boolean {
        val db = readableDatabase
        val cursor =
            db.rawQuery("SELECT COUNT(*) FROM barang WHERE kode_barang = ?", arrayOf(kodeBarang))

        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }

    fun updateStok(update: StokUpdate): Int {
        val db = this.writableDatabase
        var rowsAffected = 0

        db.beginTransaction()
        try {

            update.hargaJualBaru?.let {
                val values = ContentValues().apply { put(COLUMN_HARGA_JUAL, it) }
                val updatedRows = db.update(
                    TABLE_BARANG_MASUK, values,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(update.kodeBarang)
                )
                rowsAffected += updatedRows
            }
            update.tanggalMasukBaru?.let {
                val values = ContentValues().apply { put(COLUMN_TANGGAL_MASUK, it) }
                val updatedRows = db.update(
                    TABLE_BARANG_MASUK, values,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(update.kodeBarang)
                )
                rowsAffected += updatedRows
            }


            update.ukuranWarnaBaru?.let {
                val values = ContentValues().apply { put(COLUMN_UKURAN_WARNA, it) }
                val updatedRows = db.update(
                    TABLE_STOK, values,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(update.kodeBarang)
                )
                rowsAffected += updatedRows
            }


            update.namaTokoBaru?.let {
                val values = ContentValues().apply { put(COLUMN_NAMA_TOKO, it) }
                val updatedRows = db.update(
                    TABLE_BARANG_MASUK, values,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(update.kodeBarang)
                )
                rowsAffected += updatedRows
            }
            update.stokBaru?.let {
                val values = ContentValues().apply { put(COLUMN_STOK, it) }
                val updatedRows = db.update(
                    TABLE_STOK, values,
                    "$COLUMN_KODE_BARANG = ?",
                    arrayOf(update.kodeBarang)
                )
                rowsAffected += updatedRows
            }


            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
        }

        return rowsAffected
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
                val merekBarang = cursor.getString(1) ?: ""
                val namaToko = cursor.getString(2) ?: ""


                val dataSearch = DataSearch(idBarang, merekBarang, namaToko)
                resultList.add(dataSearch)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return resultList
    }

    fun getAllBarang(): List<DataBarangAkses> {
        val barangList = mutableListOf<DataBarangAkses>()
        val db = this.readableDatabase
        val query = """
            SELECT b.$COLUMN_KODE_BARANG, b.$COLUMN_MEREK_BARANG, b.$COLUMN_KARAKTERISTIK,
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
                val karakteristik =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KARAKTERISTIK)) ?: ""
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
                    gambar,
                    karakteristik
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

    fun deleteBarang(id: String): Int {
        val db = this.writableDatabase

        val result = db.delete(TABLE_BARANG, "$COLUMN_KODE_BARANG = ?", arrayOf(id))

        return result
    }

    fun updateBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn): Boolean {
        val db = this.writableDatabase
        db.beginTransaction()
        try {

            val valuesBarang = ContentValues().apply {
                put(COLUMN_KODE_BARANG, barang.id_barang)
                put(COLUMN_MEREK_BARANG, barang.merek_barang)
                put(COLUMN_KARAKTERISTIK, barang.karakteristik)
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
                put(COLUMN_UKURAN_WARNA, stok.ukuranwarna)
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
            SELECT b.$COLUMN_KODE_BARANG, b.$COLUMN_MEREK_BARANG,b.$COLUMN_KARAKTERISTIK,
               b.$COLUMN_GAMBAR, b.$COLUMN_LAST_UPDATE, s.$COLUMN_ID_STOK, s.$COLUMN_STOK, s.$COLUMN_UKURAN_WARNA,
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


        cursor?.use {
            if (it.moveToFirst()) {
                val idBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_KODE_BARANG)) ?: ""
                val namaBarang = it.getString(it.getColumnIndexOrThrow(COLUMN_MEREK_BARANG)) ?: ""
                val karakteristik =
                    it.getString(it.getColumnIndexOrThrow(COLUMN_KARAKTERISTIK)) ?: ""
                val gambarUri = Uri.parse(it.getString(it.getColumnIndexOrThrow(COLUMN_GAMBAR)))
                val lastupdate=it.getString(it.getColumnIndexOrThrow(COLUMN_LAST_UPDATE)) ?: ""
                val idStok = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_STOK))
                val stokJumlah = it.getInt(it.getColumnIndexOrThrow(COLUMN_STOK)) ?: 0
                val ukuranwarna =
                    it.getString(it.getColumnIndexOrThrow(COLUMN_UKURAN_WARNA))?: ""
                val idMasuk = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID_MASUK))
                val tanggalMasuk =
                    it.getString(it.getColumnIndexOrThrow(COLUMN_TANGGAL_MASUK)) ?: ""
                val hargaJual = it.getInt(it.getColumnIndexOrThrow(COLUMN_HARGA_JUAL)) ?: 0
                val namaToko = it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA_TOKO)) ?: ""


                itemBarang = ItemBarang(idBarang, namaBarang, karakteristik, gambarUri.toString(),lastupdate)
                stok = Stok(idStok, idBarang,ukuranwarna, stokJumlah )
                barangIn = BarangIn(idMasuk, idBarang, tanggalMasuk, hargaJual, namaToko)
            }
        }
        return Triple(itemBarang, stok, barangIn)
    }

    fun saveSetting(setting: SettingData) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, 1)
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

    fun searchFlexible(keyword: String): List<SearchData> {
        val db = readableDatabase
        val listBarang = mutableListOf<SearchData>()

        if (keyword.isBlank()) return listBarang


        val words = keyword.trim().lowercase().split("\\s+".toRegex())


        val conditions = mutableListOf<String>()
        val args = mutableListOf<String>()

        for (word in words) {
            val likeWord = "%$word%"

            conditions.add("LOWER(REPLACE(barang.kode_barang, ' ', '')) LIKE ?")
            args.add(likeWord)

            conditions.add("LOWER(REPLACE(${COLUMN_MEREK_BARANG}, ' ', '')) LIKE ?")
            args.add(likeWord)

            conditions.add("LOWER(REPLACE(${COLUMN_KARAKTERISTIK}, ' ', '')) LIKE ?")
            args.add(likeWord)

        }

        val whereClause = conditions.joinToString(" OR ")

        val query = """
        SELECT * FROM $TABLE_BARANG 
        LEFT JOIN $TABLE_BARANG_MASUK ON $TABLE_BARANG.$COLUMN_KODE_BARANG = $TABLE_BARANG_MASUK.$COLUMN_KODE_BARANG
        WHERE $whereClause
    """
        val cursor = db.rawQuery(query, args.toTypedArray())
        while (cursor.moveToNext()) {
            listBarang.add(
                SearchData(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_BARANG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEREK_BARANG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KARAKTERISTIK))
                )
            )
        }
        cursor.close()
        return listBarang
    }

    fun insertHistory(item: History) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_WAKTU_HISTORY, item.waktu)
                put(COLUMN_KODE_BARANG_HISTORY, item.kodeBarang)
                put(COLUMN_STOK_HISTORY, item.stok)
                put(COLUMN_JENIS_DATA_HISTORY, item.jenisData)
            }
            db.insert(TABLE_HISTORY, null, values)
            db.close()
        }
    }

    fun getAllHistoryItems(): List<History> {
        deleteOldHistory()
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
                val historyjenisData =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JENIS_DATA_HISTORY))

                val history = History(
                    idhistory,
                    historwaktu,
                    historykodeBarang,
                    historystok,
                    historyjenisData
                )
                items.add(history)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    fun deleteOldHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = writableDatabase
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val thirtyDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -30)
            }.time

            val cursor = db.rawQuery(
                "SELECT $COLUMN_ID_HISTORY, $COLUMN_WAKTU_HISTORY FROM $TABLE_HISTORY",
                null
            )

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_HISTORY))
                    val historyDateStr =
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAKTU_HISTORY))

                    try {
                        val historyDate = dateFormat.parse(historyDateStr)
                        if (historyDate != null && historyDate.before(thirtyDaysAgo)) {
                            db.delete(
                                TABLE_HISTORY,
                                "$COLUMN_ID_HISTORY = ?",
                                arrayOf(id.toString())
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    fun getBarangStokTertinggi(): String? {
        val db = this.readableDatabase
        val query = """
        SELECT 
            b.$COLUMN_MEREK_BARANG AS nama_barang,
            s.$COLUMN_STOK AS stok
        FROM $TABLE_STOK s
        JOIN $TABLE_BARANG b
        ON s.$COLUMN_KODE_BARANG = b.$COLUMN_KODE_BARANG
        ORDER BY s.$COLUMN_STOK DESC
        LIMIT 1
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        var result: String? = null

        if (cursor.moveToFirst()) {
            val nama = cursor.getString(cursor.getColumnIndexOrThrow("nama_barang"))
            val stok = cursor.getInt(cursor.getColumnIndexOrThrow("stok"))
            result = "Stok tertinggi: $nama - $stok pasang"
        }

        cursor.close()
        return result
    }

    fun getTotalBarang(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_BARANG", null)
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun getStokRendah(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT COUNT(*) FROM (
            SELECT s.$COLUMN_KODE_BARANG, SUM(s.$COLUMN_STOK) as total_stok
            FROM $TABLE_STOK s
            GROUP BY s.$COLUMN_KODE_BARANG
            HAVING total_stok <= 2
        )
        """.trimIndent(), null
        )

        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun getBarangDenganStokTerdekatHabis(limit: Int = 5): List<DataBarangHampirHabisHome> {
        val list = mutableListOf<DataBarangHampirHabisHome>()
        val db = readableDatabase
        val query = """
        SELECT b.$COLUMN_MEREK_BARANG, s.$COLUMN_STOK, b.$COLUMN_GAMBAR
        FROM $TABLE_STOK s
        JOIN $TABLE_BARANG b ON s.$COLUMN_KODE_BARANG = b.$COLUMN_KODE_BARANG
        WHERE s.$COLUMN_STOK <= 2
        ORDER BY s.$COLUMN_STOK ASC
        LIMIT ?
    """
        val cursor = db.rawQuery(query, arrayOf(limit.toString()))
        if (cursor.moveToFirst()) {
            do {
                val nama = cursor.getString(0)
                val stok = cursor.getInt(1)
                val gambar = cursor.getString(2) ?: ""
                list.add(
                    DataBarangHampirHabisHome(
                        imageUrl = gambar,
                        namaBarang = nama,
                        stok = stok
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getLastThreeHistories(): List<History> {
        val historyList = mutableListOf<History>()
        val db = readableDatabase
        val today = DateUtils.getCurrentDate()
        val query = "SELECT * FROM $TABLE_HISTORY WHERE $COLUMN_WAKTU_HISTORY LIKE '$today%' ORDER BY $COLUMN_ID_HISTORY DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_HISTORY))
                val waktu = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAKTU_HISTORY))
                val kodeBarang =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_BARANG_HISTORY))
                val stok = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STOK_HISTORY))
                val jenisData =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JENIS_DATA_HISTORY))

                historyList.add(
                    History(
                        id = id,
                        waktu = waktu,
                        kodeBarang = kodeBarang,
                        stok = stok,
                        jenisData = jenisData
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return historyList
    }

    fun ambilSemuaUriDariDatabase(): List<String> {
        val uriList = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_GAMBAR FROM $TABLE_BARANG WHERE $COLUMN_GAMBAR IS NOT NULL"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val uri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAMBAR))
                uriList.add(uri)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return uriList
    }

    fun getBarangTertinggal(database: SQLiteDatabase): List<BarangMonitor> {
        val list = mutableListOf<BarangMonitor>()

        val query = """
        SELECT 
            b.$COLUMN_KODE_BARANG,
            b.$COLUMN_MEREK_BARANG,
            s.$COLUMN_STOK,
            b.$COLUMN_LAST_UPDATE
        FROM $TABLE_BARANG b
        LEFT JOIN $TABLE_STOK s ON b.$COLUMN_KODE_BARANG = s.$COLUMN_KODE_BARANG
        ORDER BY b.$COLUMN_LAST_UPDATE ASC
    """.trimIndent()

        val cursor = database.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val kode = cursor.getString(0)
            val merek = cursor.getString(1)
            val stok = cursor.getInt(2)
            val lastUpdate = cursor.getString(3)

            list.add(
                BarangMonitor(
                    kodeBarang = kode,
                    merekBarang = merek,
                    stok = stok,
                    lastUpdate = lastUpdate
                )
            )
        }

        cursor.close()
        return list
    }


}