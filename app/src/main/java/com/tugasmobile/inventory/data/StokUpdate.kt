package com.tugasmobile.inventory.data

data class StokUpdate(
    val kodeBarang: String,
    val hargaJualBaru: Int? = null,
    val ukuranWarnaBaru: String? = null,
    val namaTokoBaru: String? = null,
    val stokBaru: Int? = null
)