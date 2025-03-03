package com.tugasmobile.inventory.data

data class BarangOut(
    val IdBrgKeluar:Long,
    val id_barang:String,
    val warna:String,
    val ukuran:String,
    val stok_keluar:Int,
    val Tgl_Keluar:String,
    val Hrg_Beli: Int
)
