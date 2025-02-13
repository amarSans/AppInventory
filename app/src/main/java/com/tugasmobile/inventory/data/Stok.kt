package com.tugasmobile.inventory.data

data class Stok(
    val id_stok:Long,
    val id_barang:Long,
    val stok: Int,
    val warna: List<String>,
    val ukuran: String,
)
