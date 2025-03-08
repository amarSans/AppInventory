package com.tugasmobile.inventory.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tugasmobile.inventory.data.ItemNotifikasi

class SyncDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "database_.db"
        private const val DATABASE_VERSION = 1

        private var instance: SyncDatabaseHelper? = null

        fun getInstance(context: Context): SyncDatabaseHelper {
            if (instance == null) {
                instance = SyncDatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE stok_sinkron (
                kode_barang TEXT PRIMARY KEY,
                stok INTEGER
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS stok_sinkron")
        onCreate(db)
    }

    fun insertData(kodeBarang: String, stok: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("kode_barang", kodeBarang)
        values.put("stok", stok)

        db.insertWithOnConflict("stok_sinkron", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }
    fun getAllStokData(): List<ItemNotifikasi> {
        val stokList = mutableListOf<ItemNotifikasi>()
        val db = readableDatabase

        // Query untuk mengambil semua data
        val cursor = db.query(
            "stok_sinkron", // Nama tabel
            arrayOf("kode_barang", "stok"), // Kolom yang diambil
            null, // Klausa WHERE (null untuk mengambil semua data)
            null, // Nilai untuk klausa WHERE
            null, null, null
        )

        // Iterasi melalui cursor dan tambahkan data ke list
        while (cursor.moveToNext()) {
            val kodeBarang = cursor.getString(cursor.getColumnIndexOrThrow("kode_barang"))
            val stok = cursor.getInt(cursor.getColumnIndexOrThrow("stok"))
            stokList.add(ItemNotifikasi(kodeBarang, stok))
        }

        cursor.close()
        return stokList
    }

}