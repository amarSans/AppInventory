package com.tugasmobile.inventory.ui.uiData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.Laporan
import com.tugasmobile.inventory.data.LaporanDatabaseHelper

class addDataViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseHelper= LaporanDatabaseHelper(application)
    private val _laporanList= MutableLiveData<List<Laporan>>()
    init {
        loadLaporan()
    }
    private fun loadLaporan(){
        _laporanList.value=databaseHelper.getAllLaporan()
    }
    fun insertLaporan(laporan: Laporan){
        databaseHelper.insertLaporan(laporan)
        loadLaporan()
    }
}