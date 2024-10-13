package com.tugasmobile.inventory.ui.uiData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.Barang
import com.tugasmobile.inventory.data.BarangDatabaseHelper

class addDataViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseHelper= BarangDatabaseHelper(application)
    private val _barangList= MutableLiveData<List<Barang>>()
    init {
        loadLaporan()
    }
    private fun loadLaporan(){
        _barangList.value=databaseHelper.getAllLaporan()
    }
    fun insertLaporan(barang: Barang){
        databaseHelper.insertLaporan(barang)
        loadLaporan()
    }
}