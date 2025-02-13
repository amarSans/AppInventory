package com.tugasmobile.inventory.data

data class BarangPrototype(
    val id: Long,
    val namaBarang: String,
    val kodeBarang: String,
    val stok: Int,
    val harga: Int,
    val warna: List<String>,
    val waktu: String,
    var nama_toko: String,
    val ukuran: String,
    var gambar: String
);