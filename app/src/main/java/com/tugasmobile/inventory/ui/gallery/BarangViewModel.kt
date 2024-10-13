package com.tugasmobile.inventory.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tugasmobile.inventory.data.Laporan
import com.tugasmobile.inventory.data.LaporanDatabaseHelper

class BarangViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseHelper= LaporanDatabaseHelper(application)
    private val _laporanList=MutableLiveData<List<Laporan>>()

    // LiveData untuk expose ke fragment atau activity
    val laporanList: LiveData<List<Laporan>> = _laporanList

    init {
        loadLaporan()
    }
    private fun loadLaporan(){
        _laporanList.value=databaseHelper.getAllLaporan()
    }

}