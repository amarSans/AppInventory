package com.tugasmobile.inventory.data

import android.net.Uri

data class Barang(
    val id: Long,
    val namaBarang: String,
    val kodeBarang: String,
    val stok: Int,
    val harga: Int,
    val warna: List<String>,
    val waktu: String,
    var kategori: String,
    val ukuran: String,
    var gambar: String
);