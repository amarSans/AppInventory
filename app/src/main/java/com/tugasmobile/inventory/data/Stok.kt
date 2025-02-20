package com.tugasmobile.inventory.data

data class Stok(
    val idStok: Long,
    val id_barang:Long,
    val stokBarang: Int,
    var warna: List<String>,
    var ukuran: String
)
