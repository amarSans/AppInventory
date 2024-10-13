package com.tugasmobile.inventory.ui.laporan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.Barang
import com.tugasmobile.inventory.data.BarangDatabaseHelper

class LaporanViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseHelper= BarangDatabaseHelper(application)
    private val _barangList=MutableLiveData<List<Barang>>()

    // LiveData untuk expose ke fragment atau activity
    val barangList: LiveData<List<Barang>> = _barangList

    init {
        loadLaporan()
    }
    private fun loadLaporan(){
        _barangList.value=databaseHelper.getAllLaporan()
    }
    /*fun insertLaporan(laporan: Barang){
        databaseHelper.insertLaporan(laporan)
        loadLaporan()
    }*/
}