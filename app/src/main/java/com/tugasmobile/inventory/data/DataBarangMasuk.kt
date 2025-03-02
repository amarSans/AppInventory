package com.tugasmobile.inventory.data

data class DataBarangMasuk(
    val id: String,
    val namaBarang: String,
    val stok: Int,
    val harga: Int,
    val warna: List<String>,
    val waktu: String,
    var nama_toko: String,
    val ukuran: String,
    var gambar: String
);