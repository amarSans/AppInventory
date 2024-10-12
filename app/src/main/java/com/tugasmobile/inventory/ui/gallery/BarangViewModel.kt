package com.tugasmobile.inventory.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tugasmobile.inventory.data.Laporan
import com.tugasmobile.inventory.data.LaporanDatabaseHelper

class BarangViewModel (private val databaseHelper: LaporanDatabaseHelper): ViewModel() {
    private val _listLaporan = MutableLiveData<List<Laporan>>()
    val listLaporan: LiveData<List<Laporan>> get() = _listLaporan

    // Fungsi untuk mengambil semua laporan
    fun loadLaporan() {
        val laporanList = databaseHelper.getAllLaporan()
        _listLaporan.value = laporanList
    }

}