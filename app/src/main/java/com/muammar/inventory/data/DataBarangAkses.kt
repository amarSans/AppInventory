package com.muammar.inventory.data

data class DataBarangAkses(
    val id: String,
    val namaBarang: String,
    val stok: Int,
    val harga: Int,
    val ukuranwarna: String,
    val waktu: String,
    var nama_toko: String,
    var gambar: String,
    var karakteristik: String
);