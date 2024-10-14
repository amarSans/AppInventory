package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.Barang
import com.tugasmobile.inventory.data.BarangDatabaseHelper

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseHelper= BarangDatabaseHelper(application)
    private val _barangList= MutableLiveData<List<Barang>>()
    val barangList: LiveData<List<Barang>> = _barangList
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
    fun deleteLaporan(id: Long) {
        databaseHelper.deleteLaporan(id)
        loadLaporan()
    }
}