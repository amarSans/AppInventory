package com.muammar.inventory.data

data class DaftarBarangKeluar(
    val kodeBarang: String,
    val stok: Int,
    val ukuranWarna: List<String>,
    val hargaJual: Int,
    val hargaTotal: Int
)

