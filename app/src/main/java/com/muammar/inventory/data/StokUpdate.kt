package com.muammar.inventory.data

data class StokUpdate(
    val kodeBarang: String,
    val hargaJualBaru: Int? = null,
    val tanggalMasukBaru: String? = null,
    val ukuranWarnaBaru: String? = null,
    val namaTokoBaru: String? = null,
    val stokBaru: Int? = null
)